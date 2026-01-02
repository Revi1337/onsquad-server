package revi1337.onsquad.member.domain.event;

import java.util.List;

public record MemberDeleteEvent(
        Long memberId,
        List<String> imageUrls
) {

}
