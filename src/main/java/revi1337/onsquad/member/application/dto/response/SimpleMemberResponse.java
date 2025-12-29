package revi1337.onsquad.member.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.result.SimpleMemberResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SimpleMemberResponse(
        Long id,
        String email,
        String nickname,
        String introduce,
        String mbti
) {

    public static SimpleMemberResponse from(Member member) {
        return new SimpleMemberResponse(
                member.getId(),
                null,
                member.getNickname().getValue(),
                member.getIntroduce() != null ? member.getIntroduce().getValue() : null,
                member.getMbti() != null ? member.getMbti().name() : ""
        );
    }

    public static SimpleMemberResponse from(SimpleMemberResult simpleMemberResult) {
        return new SimpleMemberResponse(
                simpleMemberResult.id(),
                simpleMemberResult.email() != null ? simpleMemberResult.email().getValue() : null,
                simpleMemberResult.nickname().getValue(),
                simpleMemberResult.introduce() != null ? simpleMemberResult.introduce().getValue() : null,
                simpleMemberResult.mbti() != null ? simpleMemberResult.mbti().name() : ""
        );
    }
}
