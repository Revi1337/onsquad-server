package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.CrewPolicy;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewImageService {

    private final CrewAccessor crewAccessor;
    private final ApplicationEventPublisher eventPublisher;

    public void updateImage(Long memberId, Long crewId, String newImageUrl) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureCrewImageUpdatable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }
        crew.updateImage(newImageUrl);
    }

    public void deleteImage(Long memberId, Long crewId) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureCrewImageDeletable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
            crew.deleteImage();
        }
    }
}
