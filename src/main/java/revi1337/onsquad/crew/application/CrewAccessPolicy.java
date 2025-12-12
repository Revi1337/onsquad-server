package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@RequiredArgsConstructor
@Component
public class CrewAccessPolicy {

    private final CrewRepository crewRepository;

    public void ensureCrewNameIsDuplicate(String name) {
        if (crewRepository.existsByName(new Name(name))) {
            throw new CrewBusinessException.AlreadyExists(CrewErrorCode.ALREADY_EXISTS);
        }
    }

    public Crew ensureCrewExistsAndGet(Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));
    }

    public void ensureCrewUpdatable(Crew crew, Long memberId) {
        if (crew.mismatchMemberId(memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
    }

    public void ensureCrewDeletable(Crew crew, Long memberId) {
        if (crew.mismatchMemberId(memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public void ensureCrewImageUpdatable(Crew crew, Long memberId) {
        if (crew.mismatchMemberId(memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_IMAGE_UPDATE_AUTHORITY);
        }
    }

    public void ensureCrewImageDeletable(Crew crew, Long memberId) {
        if (crew.mismatchMemberId(memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_IMAGE_DELETE_AUTHORITY);
        }
    }

    public void ensureReadStatisticAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_READ_STATISTIC_AUTHORITY);
        }
    }
}
