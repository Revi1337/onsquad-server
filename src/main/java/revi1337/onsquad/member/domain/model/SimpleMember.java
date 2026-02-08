package revi1337.onsquad.member.domain.model;

import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Introduce;
import revi1337.onsquad.member.domain.entity.vo.Mbti;
import revi1337.onsquad.member.domain.entity.vo.Nickname;

public record SimpleMember(
        Long id,
        Email email,
        Nickname nickname,
        Introduce introduce,
        Mbti mbti
) {

    public SimpleMember(Long id, Nickname nickname, Introduce introduce, Mbti mbti) {
        this(id, null, nickname, introduce, mbti);
    }
}
