package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.dto.SquadCreateDto;
import revi1337.onsquad.squad_member.domain.SquadMember;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.*;
import static revi1337.onsquad.member.error.MemberErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadService {

    private final SquadRepository squadRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;

    public void createNewSquad(SquadCreateDto dto, Long memberId) {
        memberRepository.findById(memberId)
                .map(member -> persistSquadIfMemberAndCrewIsValid(dto, member))
                .orElseThrow(() -> new MemberBusinessException.NotFound(NOTFOUND, memberId));
    }

    private Crew persistSquadIfMemberAndCrewIsValid(SquadCreateDto dto, Member member) {
        return crewRepository.findCrewWithMembersByName(new Name(dto.crewName()))
                .map(crew -> persistSquadIfCrewMemberIsValid(dto, member, crew))
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, dto.crewName()));
    }

    private Crew persistSquadIfCrewMemberIsValid(SquadCreateDto dto, Member member, Crew crew) {
        for (CrewMember crewMember : crew.getCrewMembers()) {
            if (crewMember.getMember().getId().equals(member.getId())) {
                if (crewMember.getStatus() == JoinStatus.PENDING) {
                    throw new CrewBusinessException.AlreadyRequest(ALREADY_REQUEST, dto.crewName());
                }

                persistSquadAndRegisterAdmin(dto, member, crew);
                return crew;
            }
        }

        throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT, member.getId(), dto.crewName());
    }

    private void persistSquadAndRegisterAdmin(SquadCreateDto dto, Member member, Crew crew) {
        Squad squad = dto.toEntity(member, crew);
        squad.addSquadMember(SquadMember.forAdmin(member));
        squadRepository.save(squad);
    }
}
