package revi1337.onsquad.infrastructure.network.discord;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DiscordNotificationClient {

    void sendNotification(Object message);

    void sendNotification(Object message, MultipartFile file);

    void sendNotification(Object message, List<MultipartFile> files);

    void sendNotification(Object message, String fileName, byte[] fileBytes);

}
