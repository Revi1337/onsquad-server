package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SimpleSquadParticipantDomainDto(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberDomainDto member
) {
    @QueryProjection
    public SimpleSquadParticipantDomainDto(Long id, LocalDateTime requestAt, SimpleMemberDomainDto member) {
        this.id = id;
        this.member = member;
        this.requestAt = requestAt;
    }
}
