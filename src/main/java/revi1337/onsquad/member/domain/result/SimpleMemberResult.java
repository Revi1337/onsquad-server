package revi1337.onsquad.member.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Introduce;
import revi1337.onsquad.member.domain.entity.vo.Mbti;
import revi1337.onsquad.member.domain.entity.vo.Nickname;

public record SimpleMemberResult(
        Long id,
        Email email,
        Nickname nickname,
        Introduce introduce,
        Mbti mbti
) {

    @QueryProjection
    public SimpleMemberResult(Long id, Nickname nickname, Introduce introduce, Mbti mbti) {
        this(id, null, nickname, introduce, mbti);
    }
}
