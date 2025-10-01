package io.multi.hello.api.link;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.multi.hello.api.link.dto.CreateLinkRequest;
import io.multi.hello.api.link.dto.LinkResponse;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.service.link.LinkReader;
import io.multi.hello.service.link.LinkWriter;
import io.multi.hello.service.link.dto.CreateLinkCommand;
import io.multi.hello.service.linkclick.LinkClickReader;
import io.multi.hello.service.linkclick.LinkClickWriter;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

/**
 * LinkController MockMvc 테스트
 *
 * Status Code + Response Spec 동시 검증
 */
@ExtendWith(MockitoExtension.class)
class LinkControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LinkReader linkReader;

    @Mock
    private LinkWriter linkWriter;

    @Mock
    private LinkClickReader linkClickReader;

    @Mock
    private LinkClickWriter linkClickWriter;

    @InjectMocks
    private LinkController linkController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(linkController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JavaTimeModule 등록
        ReflectionTestUtils.setField(linkController, "baseUrl", "http://localhost:8080");
    }

    // 테스트 데이터
    private final Link sampleLink = new Link(
            1L,
            "https://example.com/very-long-url",
            "abc123",
            1L,
            Instant.now().plusSeconds(3600),
            Instant.now(),
            Instant.now()
    );

    private final LinkIdentity testIdentity = new LinkIdentity(1L);

    // POST /api/v1/links - 생성
    @Test
    void createLink_validRequest_returnsCreatedWithLink() throws Exception {
        // given
        CreateLinkRequest request = new CreateLinkRequest("https://example.com/very-long-url", 1L);

        when(linkWriter.create(any(CreateLinkCommand.class))).thenReturn(sampleLink);

        // when & then - Status Code 검증
        MvcResult result = mockMvc.perform(post("/api/v1/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // then - Response Spec 검증
        String responseJson = result.getResponse().getContentAsString();
        LinkResponse response = objectMapper.readValue(responseJson, LinkResponse.class);

        assertThat(response.linkId()).isEqualTo(1L);
        assertThat(response.originalUrl()).isEqualTo("https://example.com/very-long-url");
        assertThat(response.shortCode()).isEqualTo("abc123");
        assertThat(response.shortUrl()).isEqualTo("http://localhost:8080/abc123");
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.createdAt()).isNotNull();

        verify(linkWriter).create(argThat(command ->
                command.url().equals("https://example.com/very-long-url") &&
                        command.userId() == 1L
        ));
    }

    @Test
    void createLink_invalidRequest_returnsBadRequest() throws Exception {
        // given - 빈 URL
        CreateLinkRequest request = new CreateLinkRequest("", 1L);

        // when & then
        mockMvc.perform(post("/api/v1/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(linkWriter, never()).create(any());
    }

    // GET /api/v1/links/short/{shortCode} - ShortCode 조회
    @Test
    void getLinkByShortCode_existingCode_returnsOkWithLink() throws Exception {
        // given
        when(linkReader.fromShortUrl("abc123"))
                .thenReturn(sampleLink);

        // when & then - Status Code 검증
        MvcResult result = mockMvc.perform(get("/api/v1/links/short/{shortCode}", "abc123"))
                .andExpect(status().isOk())
                .andReturn();

        // then - Response Spec 검증
        String responseJson = result.getResponse().getContentAsString();
        LinkResponse response = objectMapper.readValue(responseJson, LinkResponse.class);

        assertThat(response.shortCode()).isEqualTo("abc123");
        assertThat(response.originalUrl()).isEqualTo("https://example.com/very-long-url");
        assertThat(response.shortUrl()).isEqualTo("http://localhost:8080/abc123");

        verify(linkReader).fromShortUrl("abc123");
    }

    // DELETE /api/v1/links/{linkId} - 삭제
    @Test
    void deleteLink_existingId_returnsNoContent() throws Exception {
        // given
        doNothing().when(linkWriter).deleteById(testIdentity);

        // when & then
        mockMvc.perform(delete("/api/v1/links/{linkId}", 1L))
                .andExpect(status().isNoContent());

        verify(linkWriter).deleteById(testIdentity);
    }

    // GET /{shortCode} - 리다이렉트 (만료되지 않음)
    @Test
    void redirect_validShortCode_redirectsToOriginalUrl() throws Exception {
        // given
        Link notExpiredLink = new Link(
                1L,
                "https://example.com/target",
                "abc123",
                1L,
                Instant.now().plusSeconds(3600), // 만료 안됨
                Instant.now(),
                Instant.now()
        );

        when(linkReader.fromShortUrl("abc123")).thenReturn(notExpiredLink);
        when(linkClickWriter.record(any(LinkClick.class))).thenReturn(new LinkClick(
                1L, 1L, Instant.now(), "127.0.0.1", "Mozilla", null
        ));

        // when & then
        mockMvc.perform(get("/{shortCode}", "abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/target"));

        verify(linkReader).fromShortUrl("abc123");
        verify(linkClickWriter).record(any(LinkClick.class));
    }

    // GET /{shortCode} - 리다이렉트 (만료됨)
    @Test
    void redirect_expiredLink_throwsException() throws Exception {
        // given
        Link expiredLink = new Link(
                1L,
                "https://example.com/target",
                "abc123",
                1L,
                Instant.now().minusSeconds(3600), // 만료됨
                Instant.now(),
                Instant.now()
        );

        when(linkReader.fromShortUrl("abc123")).thenReturn(expiredLink);

        // when & then
        try {
            mockMvc.perform(get("/{shortCode}", "abc123"))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            // Controller에서 RuntimeException 발생 예상
            assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
            assertThat(e.getCause().getMessage()).contains("expired");
        }

        verify(linkReader).fromShortUrl("abc123");
        verify(linkClickWriter, never()).record(any());
    }

    // GET /{shortCode} - 만료 시간이 null (무제한)
    @Test
    void redirect_noExpirationLink_redirectsToOriginalUrl() throws Exception {
        // given
        Link noExpirationLink = new Link(
                1L,
                "https://example.com/target",
                "abc123",
                1L,
                null, // 만료 없음
                Instant.now(),
                Instant.now()
        );

        when(linkReader.fromShortUrl("abc123")).thenReturn(noExpirationLink);
        when(linkClickWriter.record(any(LinkClick.class))).thenReturn(new LinkClick(
                1L, 1L, Instant.now(), "127.0.0.1", "Mozilla", null
        ));

        // when & then
        mockMvc.perform(get("/{shortCode}", "abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/target"));

        verify(linkReader).fromShortUrl("abc123");
        verify(linkClickWriter).record(any(LinkClick.class));
    }
}
