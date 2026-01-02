package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.CrewPolicy;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.event.CrewDeleteEvent;
import revi1337.onsquad.crew.domain.repository.CrewRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewDeleteService {

    private final CrewAccessor crewAccessor;
    private final CrewRepository crewRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void deleteCrew(Long memberId, Long crewId) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureCrewDeletable(crew, memberId);
        crewRepository.deleteById(crewId);

        String crewImage = crew.hasImage() ? crew.getImageUrl() : null;
        eventPublisher.publishEvent(new CrewDeleteEvent(crewId, crewImage));
    }
}
