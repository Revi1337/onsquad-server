package revi1337.onsquad.member.application.event;

import revi1337.onsquad.member.domain.Member;

public record MemberImageDeleteEvent(
        Member member
) {
}
