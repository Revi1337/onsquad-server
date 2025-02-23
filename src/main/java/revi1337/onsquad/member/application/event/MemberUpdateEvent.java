package revi1337.onsquad.member.application.event;

public record MemberUpdateEvent(
        Long memberId,
        byte[] fileContent,
        String originalFilename
) {
}
