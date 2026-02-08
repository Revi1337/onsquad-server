package revi1337.onsquad.crew.application.dto.response;

import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.model.SimpleCrew;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

public record SimpleCrewResponse(
        Long id,
        String name,
        String introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberResponse owner
) {

    public static SimpleCrewResponse from(Crew crew) {
        return new SimpleCrewResponse(
                crew.getId(),
                crew.getName().getValue(),
                crew.getIntroduce() != null ? crew.getIntroduce().getValue() : "",
                crew.getKakaoLink() != null ? crew.getKakaoLink() : "",
                crew.getImageUrl() != null ? crew.getImageUrl() : "",
                SimpleMemberResponse.from(crew.getMember())
        );
    }

    public static SimpleCrewResponse from(SimpleCrew simpleCrew) {
        return new SimpleCrewResponse(
                simpleCrew.id(),
                simpleCrew.name(),
                simpleCrew.introduce() != null ? simpleCrew.introduce() : "",
                simpleCrew.kakaoLink() != null ? simpleCrew.kakaoLink() : "",
                simpleCrew.imageUrl() != null ? simpleCrew.imageUrl() : "",
                SimpleMemberResponse.from(simpleCrew.owner())
        );
    }
}
