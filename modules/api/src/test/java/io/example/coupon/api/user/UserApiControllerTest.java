package io.example.coupon.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.coupon.api.user.dto.UserCreateRequest;
import io.example.coupon.api.user.dto.UserResponse;
import io.example.coupon.api.user.dto.UserUpdateRequest;
import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
import io.example.coupon.service.user.UserReader;
import io.example.coupon.service.user.UserWriter;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class UserApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserReader userReader;

    @Mock
    private UserWriter userWriter;

    @InjectMocks
    private UserApiController userApiController;

    private ObjectMapper objectMapper;

    // Test data based on User model spec
    private final User sampleUser = new User(
            1L,                     // userId
            "test@example.com",     // email
            "testName",             // name
            Instant.now(),          // createdAt
            Instant.now()           // updatedAt
    );

    private final UserIdentity testIdentity = new UserIdentity(1L);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userApiController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JavaTimeModule 등록
        // Creator가 없는 클래스도 역직렬화 가능하도록 설정
        objectMapper.registerModule(new com.fasterxml.jackson.module.paramnames.ParameterNamesModule());
    }

    // POST /api/users - 생성

    @Test
    void createUser_validRequest_returnsCreatedWithUser() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("test@example.com");
        request.setName("testName");

        when(userWriter.create(any(User.class))).thenReturn(sampleUser);

        // when & then - Status Code + Response Spec 검증
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(userWriter).create(argThat(user ->
                user.getUserId() == null &&
                        user.getEmail().equals("test@example.com") &&
                        user.getName().equals("testName")
        ));
    }

    // GET /api/users/{userId} - ID 조회

    @Test
    void getUser_existingId_returnsOkWithUser() throws Exception {
        // given
        when(userReader.findById(testIdentity)).thenReturn(sampleUser);

        // when & then
        mockMvc.perform(get("/api/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(userReader).findById(testIdentity);
    }

    @Test
    void getUser_nonExistingId_returnsNotFound() throws Exception {
        // given
        UserIdentity nonExistingIdentity = new UserIdentity(999L);
        when(userReader.findById(nonExistingIdentity)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/users/{userId}", 999L))
                .andExpect(status().isNotFound());

        verify(userReader).findById(nonExistingIdentity);
    }

    // GET /api/users - 전체 조회

    @Test
    void getAllUsers_returnsOkWithList() throws Exception {
        // given
        User anotherUser = new User(2L, "another@example.com", "anotherName", Instant.now(), Instant.now());
        when(userReader.findAll()).thenReturn(List.of(sampleUser, anotherUser));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].userId").value(2));

        verify(userReader).findAll();
    }

    @Test
    void getAllUsers_emptyList_returnsOkWithEmptyArray() throws Exception {
        // given
        when(userReader.findAll()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userReader).findAll();
    }

    // PUT /api/users/{userId} - 수정

    @Test
    void updateUser_existingId_returnsOkWithUpdatedUser() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("updated@example.com");
        request.setName("updatedName");

        User updatedUser = new User(1L, "updated@example.com", "updatedName", sampleUser.getCreatedAt(), Instant.now());

        when(userReader.findById(testIdentity)).thenReturn(sampleUser);
        when(userWriter.update(any(User.class))).thenReturn(updatedUser);

        // when & then
        mockMvc.perform(put("/api/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(userReader).findById(testIdentity);
        verify(userWriter).update(any(User.class));
    }

    @Test
    void updateUser_nonExistingId_returnsNotFound() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("updated@example.com");
        request.setName("updatedName");

        UserIdentity nonExistingIdentity = new UserIdentity(999L);
        when(userReader.findById(nonExistingIdentity)).thenReturn(null);

        // when & then
        mockMvc.perform(put("/api/users/{userId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userReader).findById(nonExistingIdentity);
        verify(userWriter, never()).update(any(User.class));
    }

    // DELETE /api/users/{userId} - 삭제

    @Test
    void deleteUser_existingId_returnsNoContent() throws Exception {
        // given
        doNothing().when(userWriter).deleteById(testIdentity);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", 1L))
                .andExpect(status().isNoContent());

        verify(userWriter).deleteById(testIdentity);
    }
}
