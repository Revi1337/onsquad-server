package revi1337.onsquad.squad_request.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.result.SimpleMemberResult;

public record SquadRequestResult(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberResult member
) {

    @QueryProjection
    public SquadRequestResult(Long id, LocalDateTime requestAt, SimpleMemberResult member) {
        this.id = id;
        this.member = member;
        this.requestAt = requestAt;
    }
}
