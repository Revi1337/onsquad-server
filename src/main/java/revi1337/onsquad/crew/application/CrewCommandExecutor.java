package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.inrastructure.file.application.FileStorageManager;
import revi1337.onsquad.inrastructure.file.application.event.FileDeleteEvent;

@RequiredArgsConstructor
@Component
public class CrewCommandExecutor {

    private final ApplicationEventPublisher eventPublisher;
    private final FileStorageManager crewS3StorageManager;
    private final CrewCommandService crewCommandService;

    public void newCrew(Long memberId, CrewCreateDto dto, MultipartFile file) {
        String imageUrl = null;
        try {
            if (file != null && !file.isEmpty()) {
                imageUrl = crewS3StorageManager.upload(file);
            }
            crewCommandService.newCrew(memberId, dto, imageUrl);
        } catch (CrewBusinessException exception) {
            if (imageUrl != null) {
                eventPublisher.publishEvent(new FileDeleteEvent(imageUrl));
            }
            throw exception;
        }
    }

    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto) {
        crewCommandService.updateCrew(memberId, crewId, dto);
    }

    public void deleteCrew(Long memberId, Long crewId) {
        crewCommandService.deleteCrew(memberId, crewId);
    }

    public void updateCrewImage(Long memberId, Long crewId, MultipartFile file) {
        crewCommandService.updateCrewImage(memberId, crewId, file);
    }

    public void deleteCrewImage(Long memberId, Long crewId) {
        crewCommandService.deleteCrewImage(memberId, crewId);
    }
}
