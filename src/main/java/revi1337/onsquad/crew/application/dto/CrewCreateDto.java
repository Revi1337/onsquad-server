package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;

public record CrewCreateDto(
        String name,
        String introduce,
        String detail,
        List<String> hashTags,
        String kakaoLink
) {
    public Crew toEntity(Image image, Member member) {
        return Crew.builder()
                .name(new Name(name))
                .introduce(new Introduce(introduce))
                .detail(new Detail(detail))
                .kakaoLink(kakaoLink)
                .image(image)
                .member(member)
                .build();
    }
}
