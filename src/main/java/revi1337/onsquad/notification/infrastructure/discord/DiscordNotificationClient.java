package revi1337.onsquad.notification.infrastructure.discord;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class DiscordNotificationClient {

    public void sendNotification(String webhookEndpoint, Object message) {
        send(webhookEndpoint, createBody(message));
    }

    public void sendNotification(String webhookEndpoint, Object message, MultipartFile file) {
        send(webhookEndpoint, createBody(message, file));
    }

    public void sendNotification(String webhookEndpoint, Object message, List<MultipartFile> files) {
        send(webhookEndpoint, createBody(message, files.toArray(MultipartFile[]::new)));
    }

    public void sendNotification(String webhookEndpoint, Object message, String fileName, byte[] fileBytes) {
        send(webhookEndpoint, createBody(message, fileName, fileBytes));
    }

    private MultiValueMap<String, Object> createBody(Object message) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("payload_json", message);
        return body;
    }

    private MultiValueMap<String, Object> createBody(Object message, MultipartFile... files) {
        MultiValueMap<String, Object> body = createBody(message);
        for (int i = 0; i < files.length; i++) {
            body.add(String.format("file%s", i + 1), files[i].getResource());
        }
        return body;
    }

    private MultiValueMap<String, Object> createBody(Object message, String fileName, byte[] filesBytes) {
        MultiValueMap<String, Object> body = createBody(message);
        body.add("file1", new NamedByteArrayResource(fileName, filesBytes));
        return body;
    }

    private void send(String webhookEndpoint, MultiValueMap<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            new RestTemplate().postForEntity(webhookEndpoint, entity, Void.class);
        } catch (Exception e) {
            log.error("Discord Send Error: {}", e.getMessage());
        }
    }
}
