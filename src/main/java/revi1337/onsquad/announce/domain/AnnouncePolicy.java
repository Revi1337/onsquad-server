package revi1337.onsquad.announce.domain;

import lombok.RequiredArgsConstructor;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.error.AnnounceBusinessException;
import revi1337.onsquad.announce.error.AnnounceErrorCode;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@RequiredArgsConstructor
public class AnnouncePolicy {

    public static boolean canFixable(CrewMember crewMember) {
        return crewMember.isOwner();
    }

    public static boolean canModify(Long announceWriterId, Long currentMemberId) {
        return announceWriterId.equals(currentMemberId);
    }

    public static void ensureMatchCrew(Announce announce, Long crewId) {
        if (announce.mismatchCrewId(crewId)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public static void ensureAnnounceCreatable(CrewMember crewMember) {
        if (crewMember.isLessThenManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_CREATE_AUTHORITY);
        }
    }

    public static void ensureAnnounceUpdatable(Announce announce, CrewMember crewMember) {
        if (crewMember.isLessThenManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
        if (crewMember.isManager() && announce.mismatchMemberId(crewMember.getActualMemberId())) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
    }

    public static void ensureAnnounceDeletable(Announce announce, CrewMember crewMember) {
        if (crewMember.isLessThenManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
        if (crewMember.isManager() && announce.mismatchMemberId(crewMember.getActualMemberId())) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureAnnounceFixable(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_FIX_AUTHORITY);
        }
    }
}
