package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.application.MemberAccessPolicy;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewCommandService { // TODO 의존성이 너무 많음.

    private final MemberAccessPolicy memberAccessPolicy;
    private final CrewRepository crewRepository;
    private final CrewAccessPolicy crewAccessPolicy;
    private final CrewHashtagRepository crewHashtagRepository;
    private final SquadJpaRepository squadJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void newCrew(Long memberId, CrewCreateDto dto, String newImageUrl) {
        Member member = memberAccessPolicy.ensureMemberExistsAndGet(memberId);
        crewAccessPolicy.ensureCrewNameIsDuplicate(dto.name());
        Crew crew = crewRepository.save(Crew.create(member, dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink(), newImageUrl));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto) {
        Crew crew = crewAccessPolicy.ensureCrewExistsAndGet(crewId);
        crewAccessPolicy.ensureCrewUpdatable(crew, memberId);
        crew.update(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewHashtagRepository.deleteByCrewId(crew.getId());
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    public void deleteCrew(Long memberId, Long crewId) {
        Crew crew = crewAccessPolicy.ensureCrewExistsAndGet(crewId);
        crewAccessPolicy.ensureCrewDeletable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }
        squadJpaRepository.deleteByCrewId(crewId);
        crewRepository.deleteById(crewId);
    }

    public void updateImage(Long memberId, Long crewId, String newImageUrl) {
        Crew crew = crewAccessPolicy.ensureCrewExistsAndGet(crewId);
        crewAccessPolicy.ensureCrewImageUpdatable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }
        crew.updateImage(newImageUrl);
    }

    public void deleteImage(Long memberId, Long crewId) {
        Crew crew = crewAccessPolicy.ensureCrewExistsAndGet(crewId);
        crewAccessPolicy.ensureCrewImageDeletable(crew, memberId);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
            crew.deleteImage();
        }
    }
}
