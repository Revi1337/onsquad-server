package revi1337.onsquad.crew_member.domain;

import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

public class CrewMemberPolicy {

    public static boolean isOwner(CrewMember me) {
        return me != null && me.isOwner();
    }

    public static boolean isMe(CrewMember me, SquadMember participant) {
        return me.getMember().matchId(participant.getMember().getId());
    }

    public static boolean canKick(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotLeader();
    }

    public static boolean canLeaderDelegate(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotLeader();
    }

    public static boolean canReadSquadParticipants(CrewMember crewMember) {
        return crewMember.isOwner();
    }

    public static boolean cannotReadSquadParticipants(CrewMember crewMember) {
        return crewMember.isNotOwner();
    }

    public static boolean canMangeCrew(CrewMember crewMember) {
        return crewMember.isOwner();
    }

    public static void ensureReadParticipantsAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }
    }

    public static void ensureReadCrewStatisticAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_READ_CREW_STATISTIC_AUTHORITY);
        }
    }
}
