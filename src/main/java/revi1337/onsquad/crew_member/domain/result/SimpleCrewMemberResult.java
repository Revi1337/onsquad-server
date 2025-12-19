package revi1337.onsquad.crew_member.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew_member.domain.entity.vo.CrewRole;
import revi1337.onsquad.member.domain.entity.vo.Nickname;

public record SimpleCrewMemberResult(
        Long id,
        Nickname nickname,
        CrewRole role
) {

    @QueryProjection
    public SimpleCrewMemberResult(Long id, Nickname nickname, CrewRole role) {
        this.id = id;
        this.nickname = nickname;
        this.role = role;
    }
}
