package revi1337.onsquad.crew.application;

import static revi1337.onsquad.crew.error.CrewErrorCode.ALREADY_EXISTS;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.event.CrewImageDeleteEvent;
import revi1337.onsquad.crew.domain.event.CrewImageUpdateEvent;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;

@RequiredArgsConstructor
@Service
public class CrewCommandService { // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final SquadJpaRepository squadJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void newCrew(Long memberId, CrewCreateDto dto, String imageUrl) {
        Member member = validateMemberExistsAndGet(memberId);
        validateCrewNameIsDuplicate(dto.name());
        Crew crew = crewRepository.save(Crew.create(member, dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink(), imageUrl));
        crewHashtagRepository.batchInsert(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    @Transactional
    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId); // crew까지 같이 가져올까 말까.. 걍 내비둘까..
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
        Crew crew = crewMember.getCrew();
        crew.update(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewHashtagRepository.deleteByCrewId(crew.getId());
        crewHashtagRepository.batchInsert(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    @Transactional
    public void deleteCrew(Long memberId, Long crewId) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId); // crew까지 같이 가져올까 말까.. 걍 내비둘까..
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
        Crew crew = crewMember.getCrew();
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }
        squadJpaRepository.deleteByCrewId(crewId);
        crewRepository.deleteById(crewId);
    }

    public void updateCrewImage(Long memberId, Long crewId, MultipartFile file) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId); // crew까지 같이 가져올까 말까.. 걍 내비둘까..
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
        Crew crew = crewMember.getCrew();
        eventPublisher.publishEvent(new CrewImageUpdateEvent(crew, file));
    }

    public void deleteCrewImage(Long memberId, Long crewId) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId); // crew까지 같이 가져올까 말까.. 걍 내비둘까..
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
        Crew crew = crewMember.getCrew();
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new CrewImageDeleteEvent(crew));
        }
    }

    private Member validateMemberExistsAndGet(Long memberId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOT_FOUND));
    }

    private void validateCrewNameIsDuplicate(String crewName) {
        if (crewRepository.existsByName(new Name(crewName))) {
            throw new CrewBusinessException.AlreadyExists(ALREADY_EXISTS, crewName);
        }
    }

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) {
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }
}
