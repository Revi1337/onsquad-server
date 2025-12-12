package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

/**
 * In the Crew domain, the creator of a Crew is always its Owner. Therefore, authorization checks are performed through <b>CrewMember</b>, rather than by
 * directly inspecting the <b>Crew</b> entity.
 *
 * <ul>
 *     <li><b>Crew.member</b> is always equal to the <b>CrewMember</b> with the <b>OWNER</b> role.</li>
 *     <li>All authorization checks must rely on the CrewMember, since role and permission
 *         information exists only within CrewMember, not in Crew itself.</li>
 * </ul>
 */
@RequiredArgsConstructor
@Component
public class CrewAccessPolicy {

    private final CrewRepository crewRepository;

    public void ensureCrewNameIsDuplicate(String name) {
        if (crewRepository.existsByName(new Name(name))) {
            throw new CrewBusinessException.AlreadyExists(CrewErrorCode.ALREADY_EXISTS);
        }
    }

    public void ensureCrewUpdatable(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
    }

    public void ensureCrewDeletable(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public void ensureCrewImageUpdatable(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_IMAGE_UPDATE_AUTHORITY);
        }
    }

    public void ensureCrewImageDeletable(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_IMAGE_DELETE_AUTHORITY);
        }
    }

    public void ensureReadStatisticAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_READ_STATISTIC_AUTHORITY);
        }
    }
}
