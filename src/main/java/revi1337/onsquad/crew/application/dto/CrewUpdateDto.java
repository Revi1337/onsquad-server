package revi1337.onsquad.crew.dto;

import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.image.domain.Image;

import java.util.List;

public record CrewUpdateDto(
        String name,
        String introduce,
        String detail,
        List<String> hashTags,
        String kakaoLink
) {
    public Crew toEntity(Image image) {
        return Crew.builder()
                .name(new Name(name))
                .introduce(new Introduce(introduce))
                .detail(new Detail(detail))
                .hashTags(new HashTags(hashTags))
                .kakaoLink(kakaoLink)
                .image(image)
                .build();
    }
}
