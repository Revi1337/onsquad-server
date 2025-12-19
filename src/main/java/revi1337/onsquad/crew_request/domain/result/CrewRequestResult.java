package revi1337.onsquad.crew_request.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record CrewRequestResult(
        Long id,
        LocalDateTime requestAt
) {

    @QueryProjection
    public CrewRequestResult(Long id, LocalDateTime requestAt) {
        this.id = id;
        this.requestAt = requestAt;
    }
}
