package revi1337.onsquad.crew.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.crew.application.event.CrewUpdateEvent;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.inrastructure.s3.application.S3StorageManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrewUpdateEventListener {

    private final CrewRepository crewRepository;
    private final S3StorageManager crewS3StorageManager;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleCrewUpdateEvent(CrewUpdateEvent event) {
        try {
            Crew crew = crewRepository.getById(event.crewId());
            if (crew.hasNotImage()) {
                String imageUrl = crewS3StorageManager.updateFile(null, event.fileContent(), event.originalFilename());
                crew.updateImage(imageUrl);
            } else {
                String imageUrl = crewS3StorageManager.updateFile(
                        crew.getImageUrl(), event.fileContent(), event.originalFilename()
                );
                crew.updateImage(imageUrl);
            }
            log.debug("[크루 이미지 업데이트 성공] : crew_id = {}", event.crewId());
        } catch (Exception e) {
            log.error("[크루 이미지 업데이트 실패] : crew_id = {}", event.crewId());
        }
    }
}
