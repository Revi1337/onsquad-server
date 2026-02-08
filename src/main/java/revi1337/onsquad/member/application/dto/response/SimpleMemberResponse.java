package revi1337.onsquad.member.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.model.SimpleMember;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SimpleMemberResponse(
        Long id,
        String email,
        String nickname,
        String introduce,
        String mbti
) {

    public static final SimpleMemberResponse DELETED_MEMBER = new SimpleMemberResponse(null, null, "탈퇴한 회원입니다.", null, null);

    public static SimpleMemberResponse from(Member member) {
        return new SimpleMemberResponse(
                member.getId(),
                null,
                member.getNickname().getValue(),
                member.getIntroduce() != null ? member.getIntroduce().getValue() : null,
                member.getMbti() != null ? member.getMbti().name() : ""
        );
    }

    public static SimpleMemberResponse from(SimpleMember simpleMember) {
        return new SimpleMemberResponse(
                simpleMember.id(),
                simpleMember.email() != null ? simpleMember.email().getValue() : null,
                simpleMember.nickname().getValue(),
                simpleMember.introduce() != null ? simpleMember.introduce().getValue() : null,
                simpleMember.mbti() != null ? simpleMember.mbti().name() : ""
        );
    }
}
