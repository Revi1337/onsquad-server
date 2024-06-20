package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewCreateDto;
import revi1337.onsquad.crew.dto.CrewJoinDto;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.List;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;

    public boolean checkDuplicateNickname(String crewName) {
        return crewRepository.existsByName(new Name(crewName));
    }

    @Transactional
    public void createNewCrew(CrewCreateDto crewCreateDto, Long memberId, byte[] image) {
        memberRepository.findById(memberId)
                .ifPresent(member -> crewRepository.findByName(new Name(crewCreateDto.name()))
                        .ifPresentOrElse(
                                ignored -> { throw new CrewBusinessException.AlreadyExists(ALREADY_EXISTS, crewCreateDto.name()); },
                                () -> crewRepository.save(crewCreateDto.toEntity(new Image(image), member))
                        )
                );
    }

    public CrewWithMemberAndImageDto findCrewByName(String crewName) {
        return crewRepository.findCrewByName(new Name(crewName))
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, crewName));
    }

    public List<CrewWithMemberAndImageDto> findCrewsByName() {
        return crewRepository.findCrewsByName();
    }

    @Transactional
    public void joinCrew(CrewJoinDto crewJoinDto, Long memberId) {
        memberRepository.findById(memberId)
                .ifPresent(member -> crewRepository.findByName(new Name(crewJoinDto.crewName()))
                        .ifPresentOrElse(
                                crew -> retrieveAndJoinCrewMember(member, crew),
                                () -> { throw new CrewBusinessException.CannotJoin(CANNOT_JOIN, crewJoinDto.crewName()); }
                        )
                );
    }

    private void retrieveAndJoinCrewMember(Member member, Crew crew) {
        crewMemberRepository.findCrewMemberByMemberId(member.getId())
                .ifPresentOrElse(
                        crewMember -> judgementCurrentJoinStatus(crewMember, crew.getName()),
                        () -> crewMemberRepository.save(CrewMember.of(crew, member))
                );
    }

    private void judgementCurrentJoinStatus(CrewMember crewMember, Name name) {
        if (crewMember.getStatus() == JoinStatus.ACCEPT) {
            throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, name.getValue());
        }

        if (crewMember.getStatus() == JoinStatus.PENDING) {
            throw new CrewBusinessException.AlreadyRequest(ALREADY_REQUEST, name.getValue());
        }
    }
}
