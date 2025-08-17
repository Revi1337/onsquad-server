package revi1337.onsquad.member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Introduce;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;

public record SimpleMemberDomainDto(
        Long id,
        Email email,
        Nickname nickname,
        Introduce introduce,
        Mbti mbti
) {
    @QueryProjection
    public SimpleMemberDomainDto(Long id, Nickname nickname, Introduce introduce, Mbti mbti) {
        this(id, null, nickname, introduce, mbti);
    }
}
