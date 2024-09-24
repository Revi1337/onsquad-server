package revi1337.onsquad.member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;

public record SimpleMemberInfoDomainDto(
        Long id,
        Email email,
        Nickname nickname,
        Mbti mbti
) {
    @QueryProjection
    public SimpleMemberInfoDomainDto(Long id, Email email, Nickname nickname, Mbti mbti) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.mbti = mbti;
    }

    @QueryProjection
    public SimpleMemberInfoDomainDto(Long id, Nickname nickname, Mbti mbti) {
        this(id, null, nickname, mbti);
    }

    @QueryProjection
    public SimpleMemberInfoDomainDto(Nickname nickname) {
        this(null, null, nickname, null);
    }
}
