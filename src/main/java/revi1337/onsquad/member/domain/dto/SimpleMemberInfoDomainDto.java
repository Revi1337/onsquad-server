package revi1337.onsquad.member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;

public record SimpleMemberInfoDomainDto(
        Long id,
        Email email,
        Nickname nickname
) {
    @QueryProjection
    public SimpleMemberInfoDomainDto(Long id, Email email, Nickname nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

    @QueryProjection
    public SimpleMemberInfoDomainDto(Long id, Nickname nickname) {
        this(id, null, nickname);
    }

    @QueryProjection
    public SimpleMemberInfoDomainDto(Nickname nickname) {
        this(null, null, nickname);
    }
}
