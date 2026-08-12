package revi1337.onsquad.crew.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.domain.CrewPolicy;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.model.CrewCreateSpec;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.application.MemberAccessor;
import revi1337.onsquad.member.domain.entity.Member;

@Service
@Transactional
@RequiredArgsConstructor
public class CrewCommandService {

    private final MemberAccessor memberAccessor;
    private final CrewAccessor crewAccessor;
    private final CrewRepository crewRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final CrewContextHandler crewContextHandler;
    private final ApplicationEventPublisher eventPublisher;

    public Long newCrew(Long memberId, CrewCreateDto dto, String imageUrl) {
        Member owner = memberAccessor.getById(memberId);
        crewAccessor.validateCrewNameIsDuplicate(dto.name());
        CrewCreateSpec spec = dto.toSpec(owner, imageUrl);
        Crew crew = crewRepository.save(Crew.create(spec, LocalDateTime.now()));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(spec.getHashtags()));
        return crew.getId();
    }

    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureModifiable(crew, memberId);
        crew.update(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewHashtagRepository.deleteByCrewIdIn(List.of(crew.getId()));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    public void updateImage(Long memberId, Long crewId, String imageUrl) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureImageModifiable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }
        crew.updateImage(imageUrl);
    }

    public void deleteImage(Long memberId, Long crewId) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureImageDeletable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
            crew.deleteImage();
        }
    }

    public void deleteCrew(Long memberId, Long crewId) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureDeletable(crew, memberId);
        crewContextHandler.disposeContextWithSquads(crew);
    }
}
