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
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
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
    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
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
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewAccessPolicy.ensureCrewUpdatable(crewMember);
        Crew crew = crewMember.getCrew();
        crew.update(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewHashtagRepository.deleteByCrewId(crew.getId());
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    @Transactional
    public void deleteCrew(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewAccessPolicy.ensureCrewDeletable(crewMember);
        Crew crew = crewMember.getCrew();
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }
        squadJpaRepository.deleteByCrewId(crewId);
        crewRepository.deleteById(crewId);
    }

    public void updateCrewImage(Long memberId, Long crewId, MultipartFile file) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewAccessPolicy.ensureCrewImageUpdatable(crewMember);
        Crew crew = crewMember.getCrew();
        eventPublisher.publishEvent(new CrewImageUpdateEvent(crew, file));
    }

    public void deleteCrewImage(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewAccessPolicy.ensureCrewImageDeletable(crewMember);
        Crew crew = crewMember.getCrew();
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new CrewImageDeleteEvent(crew));
        }
    }
}
