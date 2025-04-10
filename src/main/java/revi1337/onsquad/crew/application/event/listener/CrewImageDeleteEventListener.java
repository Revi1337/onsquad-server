package revi1337.onsquad.crew.application.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.application.event.CrewImageDeleteEvent;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.inrastructure.file.application.RecycleBin;

@RequiredArgsConstructor
@Component
public class CrewImageDeleteEventListener {

    private final CrewRepository crewRepository;

    @EventListener(CrewImageDeleteEvent.class)
    public void handleCrewImageDeleteEvent(CrewImageDeleteEvent event) {
        Crew crew = event.crew();
        RecycleBin.append(crew.getImageUrl());
        crew.deleteImage();
        crewRepository.saveAndFlush(crew);
    }
}
