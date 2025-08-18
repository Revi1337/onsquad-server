package revi1337.onsquad.crew.application;

import static revi1337.onsquad.crew.error.CrewErrorCode.ALREADY_EXISTS;
import static revi1337.onsquad.crew.error.CrewErrorCode.INVALID_PUBLISHER;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.application.event.CrewImageDeleteEvent;
import revi1337.onsquad.crew.application.event.CrewImageUpdateEvent;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtagRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.inrastructure.file.application.event.FileDeleteEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.squad.domain.SquadJpaRepository;

@RequiredArgsConstructor
@Service
public class CrewCommandService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final SquadJpaRepository squadJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long newCrew(Long memberId, CrewCreateDto dto, String imageUrl) {
        Member member = memberRepository.getById(memberId);
        checkCrewNameIsDuplicate(dto.name());

        Crew crew = Crew.create(member, dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink(), imageUrl);
        Crew persistedCrew = crewRepository.save(crew);
        crewHashtagRepository.batchInsert(persistedCrew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
        return persistedCrew.getId();
    }

    @Transactional
    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto) {
        CrewMember crewMember = crewMemberRepository.getWithCrewByCrewIdAndMemberId(crewId, memberId);
        Crew crew = crewMember.getCrew();
        checkMemberIsCrewOwner(crew.getId(), crewMember);

        crew.update(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewHashtagRepository.deleteByCrewId(crew.getId());
        crewHashtagRepository.batchInsert(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    @Transactional
    public void deleteCrew(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberRepository.getWithCrewByCrewIdAndMemberId(crewId, memberId);
        Crew crew = crewMember.getCrew();
        checkMemberIsCrewOwner(crewId, crewMember);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new FileDeleteEvent(crew.getImageUrl()));
        }

        squadJpaRepository.deleteByCrewId(crewId);
        crewRepository.deleteById(crewId);
    }

    public void updateCrewImage(Long memberId, Long crewId, MultipartFile file) {
        CrewMember crewMember = crewMemberRepository.getWithCrewByCrewIdAndMemberId(crewId, memberId);
        Crew crew = crewMember.getCrew();
        checkMemberIsCrewOwner(crewId, crewMember);

        eventPublisher.publishEvent(new CrewImageUpdateEvent(crew, file));
    }

    public void deleteCrewImage(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberRepository.getWithCrewByCrewIdAndMemberId(crewId, memberId);
        Crew crew = crewMember.getCrew();
        checkMemberIsCrewOwner(crewId, crewMember);
        if (crew.hasImage()) {
            eventPublisher.publishEvent(new CrewImageDeleteEvent(crew));
        }
    }

    private void checkCrewNameIsDuplicate(String crewName) {
        if (crewRepository.existsByName(new Name(crewName))) {
            throw new CrewBusinessException.AlreadyExists(ALREADY_EXISTS, crewName);
        }
    }

    private void checkMemberIsCrewOwner(Long crewId, CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, crewId);
        }
    }
}
