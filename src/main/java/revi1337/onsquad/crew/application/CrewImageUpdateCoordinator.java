package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.application.file.FileStorageManager;
import revi1337.onsquad.crew.error.CrewBusinessException;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;

@RequiredArgsConstructor
@Component
public class CrewImageUpdateCoordinator {

    private final FileStorageManager crewS3StorageManager;
    private final CrewImageService crewImageService;
    private final ApplicationEventPublisher eventPublisher;

    public void updateImage(Long memberId, Long crewId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        String imageUrl = null;
        try {
            imageUrl = crewS3StorageManager.upload(file);
            crewImageService.updateImage(memberId, crewId, imageUrl);
        } catch (CrewBusinessException exception) {
            if (imageUrl != null) {
                eventPublisher.publishEvent(new FileDeleteEvent(imageUrl));
            }
            throw exception;
        }
    }
}
