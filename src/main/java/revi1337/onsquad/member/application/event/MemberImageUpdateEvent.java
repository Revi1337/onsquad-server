package revi1337.onsquad.member.application.event;

import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.member.domain.Member;

public record MemberImageUpdateEvent(
        Member member,
        MultipartFile file
) {
}
