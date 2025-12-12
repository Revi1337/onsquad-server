package revi1337.onsquad.announce.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.announce.error.AnnounceErrorCode;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@RequiredArgsConstructor
@Component
public class AnnounceAccessPolicy {

    private final AnnounceRepository announceRepository;

    public Announce ensureAnnounceExistsAndGet(Long announceId) {
        return announceRepository.findById(announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }

    public void ensureMatchCrew(Announce announce, Long crewId) {
        if (announce.mismatchCrewId(crewId)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public void ensureAnnounceCreatable(CrewMember crewMember) {
        if (crewMember.isLessThenManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_CREATE_AUTHORITY);
        }
    }

    public void ensureAnnounceUpdatable(Announce announce, CrewMember crewMember) {
        if (crewMember.isLessThenManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
        if (crewMember.isManager() && announce.mismatchMemberId(crewMember.getId())) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
    }

    public void ensureAnnounceDeletable(Announce announce, CrewMember crewMember) {
        if (crewMember.isLessThenManager()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
        if (crewMember.isManager() && announce.mismatchMemberId(crewMember.getId())) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public void ensureAnnounceFixable(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_FIX_AUTHORITY);
        }
    }
}
