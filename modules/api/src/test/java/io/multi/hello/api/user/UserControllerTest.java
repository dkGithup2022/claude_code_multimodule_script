package io.multi.hello.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.multi.hello.api.user.dto.CreateUserRequest;
import io.multi.hello.api.user.dto.UpdateUserRequest;
import io.multi.hello.api.user.dto.UserResponse;
import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import io.multi.hello.service.user.UserReader;
import io.multi.hello.service.user.UserWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController MockMvc 테스트
 *
 * Status Code + Response Spec 동시 검증
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserReader userReader;

    @Mock
    private UserWriter userWriter;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JavaTimeModule 등록
    }

    // 테스트 데이터
    private final User sampleUser = new User(
            1L,
            "test@example.com",
            "testName",
            Instant.now(),
            Instant.now()
    );

    private final UserIdentity testIdentity = new UserIdentity(1L);

    // POST /api/v1/users - 생성
    @Test
    void createUser_validRequest_returnsCreatedWithUser() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest("test@example.com", "testName");

        when(userWriter.upsert(any(User.class))).thenReturn(sampleUser);

        // when & then - Status Code 검증
        MvcResult result = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // then - Response Spec 검증
        String responseJson = result.getResponse().getContentAsString();
        UserResponse response = objectMapper.readValue(responseJson, UserResponse.class);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.name()).isEqualTo("testName");
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();

        verify(userWriter).upsert(argThat(user ->
                user.getUserId() == null &&
                        user.getEmail().equals("test@example.com") &&
                        user.getName().equals("testName")
        ));
    }

    @Test
    void createUser_invalidRequest_returnsBadRequest() throws Exception {
        // given - 빈 이메일
        CreateUserRequest request = new CreateUserRequest("", "testName");

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userWriter, never()).upsert(any());
    }

    // GET /api/v1/users/{userId} - ID 조회
    @Test
    void getUser_existingId_returnsOkWithUser() throws Exception {
        // given
        when(userReader.findById(testIdentity))
                .thenReturn(Optional.of(sampleUser));

        // when & then - Status Code 검증
        MvcResult result = mockMvc.perform(get("/api/v1/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        // then - Response Spec 검증
        String responseJson = result.getResponse().getContentAsString();
        UserResponse response = objectMapper.readValue(responseJson, UserResponse.class);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.name()).isEqualTo("testName");

        verify(userReader).findById(testIdentity);
    }

    @Test
    void getUser_nonExistingId_throwsException() throws Exception {
        // given
        when(userReader.findById(new UserIdentity(999L)))
                .thenReturn(Optional.empty());

        // when & then
        try {
            mockMvc.perform(get("/api/v1/users/{userId}", 999L))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            // Controller에서 RuntimeException 발생 예상
            assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
        }

        verify(userReader).findById(new UserIdentity(999L));
    }

    // GET /api/v1/users/email/{email} - 이메일 조회
    @Test
    void getUserByEmail_existingEmail_returnsOkWithUser() throws Exception {
        // given
        when(userReader.findByEmail("test@example.com"))
                .thenReturn(sampleUser);

        // when & then - Status Code 검증
        MvcResult result = mockMvc.perform(get("/api/v1/users/email/{email}", "test@example.com"))
                .andExpect(status().isOk())
                .andReturn();

        // then - Response Spec 검증
        String responseJson = result.getResponse().getContentAsString();
        UserResponse response = objectMapper.readValue(responseJson, UserResponse.class);

        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.name()).isEqualTo("testName");

        verify(userReader).findByEmail("test@example.com");
    }

    // GET /api/v1/users/name/{name} - 이름 조회
    @Test
    void getUserByName_existingName_returnsOkWithUser() throws Exception {
        // given
        when(userReader.findByName("testName"))
                .thenReturn(sampleUser);

        // when & then - Status Code 검증
        MvcResult result = mockMvc.perform(get("/api/v1/users/name/{name}", "testName"))
                .andExpect(status().isOk())
                .andReturn();

        // then - Response Spec 검증
        String responseJson = result.getResponse().getContentAsString();
        UserResponse response = objectMapper.readValue(responseJson, UserResponse.class);

        assertThat(response.name()).isEqualTo("testName");
        assertThat(response.email()).isEqualTo("test@example.com");

        verify(userReader).findByName("testName");
    }

    // PUT /api/v1/users/{userId} - 수정
    @Test
    void updateUser_existingId_returnsOkWithUpdatedUser() throws Exception {
        // given
        UpdateUserRequest request = new UpdateUserRequest("updatedName");
        User updatedUser = new User(
                1L,
                "test@example.com",
                "updatedName",
                sampleUser.getCreatedAt(),
                Instant.now()
        );

        when(userReader.findById(testIdentity))
                .thenReturn(Optional.of(sampleUser));
        when(userWriter.upsert(any(User.class)))
                .thenReturn(updatedUser);

        // when & then - Status Code 검증
        MvcResult result = mockMvc.perform(put("/api/v1/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // then - Response Spec 검증
        String responseJson = result.getResponse().getContentAsString();
        UserResponse response = objectMapper.readValue(responseJson, UserResponse.class);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("updatedName");
        assertThat(response.email()).isEqualTo("test@example.com");

        verify(userReader).findById(testIdentity);
        verify(userWriter).upsert(argThat(user ->
                user.getUserId().equals(1L) &&
                        user.getName().equals("updatedName")
        ));
    }

    @Test
    void updateUser_nonExistingId_throwsException() throws Exception {
        // given
        UpdateUserRequest request = new UpdateUserRequest("updatedName");

        when(userReader.findById(new UserIdentity(999L)))
                .thenReturn(Optional.empty());

        // when & then
        try {
            mockMvc.perform(put("/api/v1/users/{userId}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            // Controller에서 RuntimeException 발생 예상
            assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
        }

        verify(userReader).findById(new UserIdentity(999L));
        verify(userWriter, never()).upsert(any());
    }

    // DELETE /api/v1/users/{userId} - 삭제
    @Test
    void deleteUser_existingId_returnsNoContent() throws Exception {
        // given
        doNothing().when(userWriter).deleteById(testIdentity);

        // when & then
        mockMvc.perform(delete("/api/v1/users/{userId}", 1L))
                .andExpect(status().isNoContent());

        verify(userWriter).deleteById(testIdentity);
    }
}