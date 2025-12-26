package revi1337.onsquad.crew.domain;

import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.error.CrewBusinessException;
import revi1337.onsquad.crew.error.CrewErrorCode;

@RequiredArgsConstructor
public class CrewPolicy {

    public static void ensureCrewUpdatable(Crew crew, Long memberId) {
        if (crew.mismatchMemberId(memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
    }

    public static void ensureCrewDeletable(Crew crew, Long memberId) {
        if (crew.mismatchMemberId(memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureCrewImageUpdatable(Crew crew, Long memberId) {
        if (crew.mismatchMemberId(memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_IMAGE_UPDATE_AUTHORITY);
        }
    }

    public static void ensureCrewImageDeletable(Crew crew, Long memberId) {
        if (crew.mismatchMemberId(memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_IMAGE_DELETE_AUTHORITY);
        }
    }
}
