package revi1337.onsquad.crew.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.application.file.FileStorageManager;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.event.CrewImageDeleteEvent;
import revi1337.onsquad.crew.domain.event.CrewImageUpdateEvent;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.infrastructure.aws.s3.support.RecycleBin;

@RequiredArgsConstructor
@Component
public class CrewImageListener {

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

    @EventListener(CrewImageDeleteEvent.class)
    public void handleCrewImageDeleteEvent(CrewImageDeleteEvent event) {
        Crew crew = event.crew();
        RecycleBin.append(crew.getImageUrl());
        crew.deleteImage();
        crewRepository.saveAndFlush(crew);
    }
}
