package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.CrewPolicy;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.squad.domain.repository.SquadRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewDeleteService {

    private final CrewAccessor crewAccessor;
    private final SquadRepository squadRepository;
    private final CrewRepository crewRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void deleteCrew(Long memberId, Long crewId) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureCrewDeletable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }
        squadRepository.deleteByCrewId(crewId);
        crewRepository.deleteById(crewId);
    }
}
