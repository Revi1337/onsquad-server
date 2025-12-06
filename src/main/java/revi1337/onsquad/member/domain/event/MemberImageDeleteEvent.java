package revi1337.onsquad.member.domain.event;

import revi1337.onsquad.member.domain.entity.Member;

public record MemberImageDeleteEvent(
        Member member
) {

}
