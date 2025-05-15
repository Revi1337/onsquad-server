package revi1337.onsquad.crew_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record CrewRequestDomainDto(
        Long id,
        LocalDateTime requestAt
) {
    @QueryProjection
    public CrewRequestDomainDto(Long id, LocalDateTime requestAt) {
        this.id = id;
        this.requestAt = requestAt;
    }
}
