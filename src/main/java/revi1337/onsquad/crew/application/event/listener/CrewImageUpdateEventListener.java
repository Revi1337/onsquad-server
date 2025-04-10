package revi1337.onsquad.crew.application.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.application.event.CrewImageUpdateEvent;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.inrastructure.file.application.FileStorageManager;

@RequiredArgsConstructor
@Component
public class CrewImageUpdateEventListener {

    private final CrewRepository crewRepository;
    private final FileStorageManager crewS3StorageManager;

    @EventListener(CrewImageUpdateEvent.class)
    public void handleCrewImageUpdateEvent(CrewImageUpdateEvent event) {
        Crew crew = event.crew();
        MultipartFile file = event.multipartFile();

        if (crew.hasImage()) {
            crewS3StorageManager.upload(file, crew.getImageUrl());
            return;
        }

        String uploadUrl = crewS3StorageManager.upload(file);
        crew.updateImage(uploadUrl);
        crewRepository.saveAndFlush(crew);
    }
}
