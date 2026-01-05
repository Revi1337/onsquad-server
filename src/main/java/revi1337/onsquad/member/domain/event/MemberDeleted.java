package revi1337.onsquad.member.domain.event;

public record MemberDeleted(
        Long memberId,
        String memberImageUrl
) {

}
