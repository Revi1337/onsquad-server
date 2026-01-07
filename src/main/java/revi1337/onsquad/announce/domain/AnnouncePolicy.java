package revi1337.onsquad.announce.domain;

import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.error.AnnounceBusinessException;
import revi1337.onsquad.announce.error.AnnounceErrorCode;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

public class AnnouncePolicy {

    /**
     * Managers or higher can write new announcements.
     */
    public static boolean canWrite(CrewMember me) {
        return me.isManagerOrHigher();
    }

    /**
     * Only the crew owner is authorized to pin announcements.
     */
    public static boolean canFixable(CrewMember me) {
        return me.isOwner();
    }

    /**
     * Determines if the user has permission to modify the announcement.
     * <ol>
     * <li>Crew Owners can modify any announcement.</li>
     * <li>Managers or higher can modify their own announcements.</li>
     * <li>Managers or higher can modify announcements whose authors have withdrawn (orphan posts). In this case, the first person to edit the post becomes the new author.</li>
     * </ol>
     */
    public static boolean canModify(CrewMember me, Long announceWriterId) {
        if (me.isOwner()) {
            return true;
        }
        if (me.isManagerOrHigher() && announceWriterId == null) {
            return true;
        }
        if (me.isManagerOrHigher() && me.getActualMemberId().equals(announceWriterId)) {
            return true;
        }
        return false;
    }

    public static void ensureMatchCrew(Announce announce, Long crewId) {
        if (announce.mismatchCrewId(crewId)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public static void ensureWritable(CrewMember crewMember) {
        if (crewMember.isLowerThanManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_CREATE_AUTHORITY);
        }
    }

    public static void ensureModifiable(Announce announce, CrewMember crewMember) {
        if (crewMember.isLowerThanManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
        if (crewMember.isManager() && announce.mismatchMemberId(crewMember.getActualMemberId())) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
    }

    public static void ensureDeletable(Announce announce, CrewMember crewMember) {
        if (crewMember.isLowerThanManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
        if (crewMember.isManager() && announce.mismatchMemberId(crewMember.getActualMemberId())) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureFixable(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_FIX_AUTHORITY);
        }
    }
}
