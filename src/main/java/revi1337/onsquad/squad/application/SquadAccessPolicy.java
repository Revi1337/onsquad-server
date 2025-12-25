package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.error.SquadBusinessException;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@RequiredArgsConstructor
@Component
public class SquadAccessPolicy {

    private final SquadRepository squadRepository;

    public Squad ensureSquadExistsAndGet(Long squadId) {
        return squadRepository.findById(squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
    }

    public void ensureMatchCrew(Squad squad, Long crewId) {
        if (squad.mismatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public void ensureDeletable(SquadMember squadMember) {
        if (squadMember.isNotLeader()) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public void ensureDeletable(Squad squad, Long memberId) {
        if (squad.mismatchMemberId(memberId)) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public void ensureSquadManageListAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
    }

    public boolean canSeeParticipants(CrewMember crewMember) {
        return crewMember != null && crewMember.isOwner();
    }

    public boolean isLeader(SquadMember squadMember) {
        return squadMember != null && squadMember.isLeader();
    }

    public boolean isParticipant(Long squadId, SquadMember squadMember) {
        return squadMember != null && squadMember.matchSquadId(squadId);
    }

    public boolean isLastMemberRemaining(Squad squad) {
        return squad.getCurrentSize() == 1;
    }
}
