package revi1337.onsquad.crew.dto;

import revi1337.onsquad.crew_member.domain.vo.JoinStatus;

public record CrewAcceptDto(
        String requestCrewName,
        Long requestMemberId,
        JoinStatus requestStatus
) {
}
