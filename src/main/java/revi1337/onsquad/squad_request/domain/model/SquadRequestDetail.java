package revi1337.onsquad.squad_request.domain.model;

import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.model.SimpleMember;

public record SquadRequestDetail(
        Long id,
        LocalDateTime requestAt,
        SimpleMember member
) {

    public SquadRequestDetail(Long id, LocalDateTime requestAt, SimpleMember member) {
        this.id = id;
        this.member = member;
        this.requestAt = requestAt;
    }
}
