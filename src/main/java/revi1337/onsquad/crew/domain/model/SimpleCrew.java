package revi1337.onsquad.crew.domain.model;

import revi1337.onsquad.member.domain.result.SimpleMemberResult;

public record SimpleCrew(
        Long id,
        String name,
        String introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberResult owner
) {

}
