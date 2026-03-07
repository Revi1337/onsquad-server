package revi1337.onsquad.infrastructure.aws.s3.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage;
import revi1337.onsquad.infrastructure.network.discord.DiscordNotificationClient;

@ExtendWith(MockitoExtension.class)
class S3FailNotificationProviderTest {

    @Mock
    private DiscordNotificationClient s3DiscordNotificationClient;

    @Mock
    private ObjectMapper defaultObjectMapper;

    @Mock
    private OnsquadProperties onsquadProperties;

    @InjectMocks
    private S3FailNotificationProvider provider;

    @Test
    @DisplayName("재시도 횟수 초과 시 파일 경로가 담긴 JSON 파일을 첨부하여 디스코드 알림을 전송한다")
    void sendExceedRetryAlertWithFile() throws JsonProcessingException {
        // given
        List<String> paths = List.of("member/file-1.txt", "member/file-2.txt");
        byte[] mockBytes = "mock-json-bytes".getBytes();

        when(onsquadProperties.getIdentifier()).thenReturn("local-test");
        when(defaultObjectMapper.writeValueAsBytes(any(S3FailNotificationProvider.RetryExceedJson.class)))
                .thenReturn(mockBytes);

        ArgumentCaptor<DiscordMessage> messageCaptor = ArgumentCaptor.forClass(DiscordMessage.class);
        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> fileBytesCaptor = ArgumentCaptor.forClass(byte[].class);

        // when
        provider.sendExceedRetryAlert(paths);

        // then
        assertSoftly(softly -> {
            verify(s3DiscordNotificationClient, times(1))
                    .sendNotification(messageCaptor.capture(), fileNameCaptor.capture(), fileBytesCaptor.capture());

            DiscordMessage sentMessage = messageCaptor.getValue();
            softly.assertThat(sentMessage.getUsername()).isEqualTo("Amazon Web Service");
            softly.assertThat(sentMessage.getContent()).contains("Max Retries Exceeded");
            softly.assertThat(sentMessage.getContent()).contains("`2 Objects Exceeded`");

            softly.assertThat(fileNameCaptor.getValue()).isEqualTo("exceed_file_paths.json");
            softly.assertThat(fileBytesCaptor.getValue()).isEqualTo(mockBytes);

            softly.assertThat(sentMessage.getEmbeds()).hasSize(1);
            softly.assertThat(sentMessage.getEmbeds().get(0).getTitle()).isEqualTo("S3 Deletion Process: Max Retries Reached");
        });
    }

    @Test
    @DisplayName("JSON 직렬화 실패 시 파일을 첨부하지 않고 메시지만 전송한다")
    void sendExceedRetryAlert_fallbackOnJsonError() throws JsonProcessingException {
        // given
        List<String> paths = List.of("member/error.txt");
        when(onsquadProperties.getIdentifier()).thenReturn("local-test");
        when(defaultObjectMapper.writeValueAsBytes(any())).thenThrow(new JsonProcessingException("Mock Error") {
        });

        // when
        provider.sendExceedRetryAlert(paths);

        // then
        assertSoftly(softly -> {
            verify(s3DiscordNotificationClient, times(1)).sendNotification(any(DiscordMessage.class));
            verify(s3DiscordNotificationClient, never()).sendNotification(any(), anyString(), any());
        });
    }
}
