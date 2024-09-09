package revi1337.onsquad.crew_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record CrewParticipantDomainDto(
        Long id,
        LocalDateTime requestAt
) {
    @QueryProjection
    public CrewParticipantDomainDto(Long id, LocalDateTime requestAt) {
        this.id = id;
        this.requestAt = requestAt;
    }
}
