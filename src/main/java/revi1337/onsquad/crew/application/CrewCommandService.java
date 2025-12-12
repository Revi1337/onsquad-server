package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.event.CrewImageDeleteEvent;
import revi1337.onsquad.crew.domain.event.CrewImageUpdateEvent;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.application.MemberAccessPolicy;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;

@RequiredArgsConstructor
@Service
public class CrewCommandService { // TODO 의존성이 너무 큼. Event 로 분리 필요.

    private final MemberAccessPolicy memberAccessPolicy;
    private final CrewRepository crewRepository;
    private final CrewAccessPolicy crewAccessPolicy;
    private final CrewHashtagRepository crewHashtagRepository;
    private final SquadJpaRepository squadJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void newCrew(Long memberId, CrewCreateDto dto, String imageUrl) {
        Member member = memberAccessPolicy.ensureMemberExistsAndGet(memberId);
        crewAccessPolicy.ensureCrewNameIsDuplicate(dto.name());
        Crew crew = crewRepository.save(Crew.create(member, dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink(), imageUrl));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    @Transactional
    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto) {
        Crew crew = crewAccessPolicy.ensureCrewExistsAndGet(crewId);
        crewAccessPolicy.ensureCrewUpdatable(crew, memberId);
        crew.update(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewHashtagRepository.deleteByCrewId(crew.getId());
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    @Transactional
    public void deleteCrew(Long memberId, Long crewId) {
        Crew crew = crewAccessPolicy.ensureCrewExistsAndGet(crewId);
        crewAccessPolicy.ensureCrewDeletable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }
        squadJpaRepository.deleteByCrewId(crewId);
        crewRepository.deleteById(crewId);
    }

    public void updateCrewImage(Long memberId, Long crewId, MultipartFile file) {
        Crew crew = crewAccessPolicy.ensureCrewExistsAndGet(crewId);
        crewAccessPolicy.ensureCrewImageUpdatable(crew, memberId);
        eventPublisher.publishEvent(new CrewImageUpdateEvent(crew, file));
    }

    public void deleteCrewImage(Long memberId, Long crewId) {
        Crew crew = crewAccessPolicy.ensureCrewExistsAndGet(crewId);
        crewAccessPolicy.ensureCrewImageDeletable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new CrewImageDeleteEvent(crew));
        }
    }
}
