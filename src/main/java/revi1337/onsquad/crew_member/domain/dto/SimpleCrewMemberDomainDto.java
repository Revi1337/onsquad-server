package revi1337.onsquad.crew_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew_member.domain.vo.CrewRole;
import revi1337.onsquad.member.domain.vo.Nickname;

public record SimpleCrewMemberDomainDto(
        Long id,
        Nickname nickname,
        CrewRole role
) {
    @QueryProjection
    public SimpleCrewMemberDomainDto(Long id, Nickname nickname, CrewRole role) {
        this.id = id;
        this.nickname = nickname;
        this.role = role;
    }
}
