package revi1337.onsquad.squad_member.application;

import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.CANNOT_LEAVE_LEADER;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadMemberCommandService {

    private static final int LEADER_LIMIT_THRESHOLD = 2;

    private final SquadMemberRepository squadMemberRepository;
    private final CrewMemberRepository crewMemberRepository;

    public void leaveSquad(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository
                .getWithSquadBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isLeader() && squadMemberRepository.countBySquadId(squadId) >= LEADER_LIMIT_THRESHOLD) {
            throw new SquadMemberBusinessException.CannotLeaveLeader(CANNOT_LEAVE_LEADER);
        }

        squadMemberRepository.delete(squadMember);
        squadMemberRepository.flush();
        squadMember.leaveSquad();
    }
}
