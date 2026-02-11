package revi1337.onsquad.announce.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.error.AnnounceBusinessException;
import revi1337.onsquad.announce.domain.error.AnnounceErrorCode;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@NoArgsConstructor(access = PRIVATE)
public final class AnnouncePolicy {

    /**
     * Managers or higher can write new announcements.
     */
    public static boolean canWrite(CrewMember me) {
        return CrewMemberPolicy.isManagerOrHigher(me);
    }

    /**
     * Only the crew owner is authorized to pin announcements.
     */
    public static boolean canPin(CrewMember me) {
        return CrewMemberPolicy.isOwner(me);
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
        if (CrewMemberPolicy.isOwner(me)) {
            return true;
        }
        if (CrewMemberPolicy.isGeneral(me)) {
            return false;
        }
        if (CrewMemberPolicy.isManagerOrHigher(me) && announceWriterId == null) {
            return true;
        }
        if (CrewMemberPolicy.isManagerOrHigher(me) && matchWriter(me, announceWriterId)) {
            return true;
        }
        return false;
    }

    public static void ensureMatchCrew(Announce announce, Long crewId) {
        if (mismatchCrew(announce, crewId)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public static void ensureWritable(CrewMember me) {
        if (CrewMemberPolicy.isLowerThanManager(me)) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_CREATE_AUTHORITY);
        }
    }

    public static void ensureModifiable(Announce announce, CrewMember me) {
        if (mismatchCrew(announce, me)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
        if (CrewMemberPolicy.isLowerThanManager(me)) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
        if (CrewMemberPolicy.isManager(me) && mismatchWriter(announce, me)) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
    }

    public static void ensureDeletable(Announce announce, CrewMember me) {
        if (mismatchCrew(announce, me)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
        if (CrewMemberPolicy.isLowerThanManager(me)) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
        if (CrewMemberPolicy.isManager(me) && mismatchWriter(announce, me)) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensurePinnable(CrewMember me) {
        if (CrewMemberPolicy.isNotOwner(me)) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_PIN_AUTHORITY);
        }
    }

    private static boolean matchWriter(CrewMember me, Long announceWriterId) {
        return me.getMember().getId().equals(announceWriterId);
    }

    private static boolean mismatchWriter(Announce announce, CrewMember me) {
        return !announce.getMember().getId().equals(me.getMember().getId());
    }

    private static boolean mismatchCrew(Announce announce, CrewMember me) {
        return !announce.getCrew().getId().equals(me.getCrew().getId());
    }

    private static boolean mismatchCrew(Announce announce, Long crewId) {
        return !announce.getCrew().getId().equals(crewId);
    }
}
