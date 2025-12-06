package revi1337.onsquad.member.domain.event;

import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.member.domain.entity.Member;

public record MemberImageUpdateEvent(
        Member member,
        MultipartFile file
) {

}
