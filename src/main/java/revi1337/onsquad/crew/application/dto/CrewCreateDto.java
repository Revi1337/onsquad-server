package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.domain.Member;

public record CrewCreateDto(
        String name,
        String introduce,
        String detail,
        List<Hashtag> hashtags,
        String kakaoLink
) {
    public static CrewCreateDto of(
            String name,
            String introduce,
            String detail,
            List<HashtagType> hashtagTypes,
            String kakaoLink
    ) {
        List<Hashtag> hashtags = Hashtag.fromHashtagTypes(hashtagTypes);

        return new CrewCreateDto(name, introduce, detail, hashtags, kakaoLink);
    }

    public Crew toEntity(Member member) {
        return Crew.builder()
                .name(new Name(name))
                .introduce(new Introduce(introduce))
                .detail(new Detail(detail))
                .kakaoLink(kakaoLink)
                .member(member)
                .build();
    }

    public Crew toEntity(String image, Member member) {
        return Crew.builder()
                .name(new Name(name))
                .introduce(new Introduce(introduce))
                .detail(new Detail(detail))
                .kakaoLink(kakaoLink)
                .imageUrl(image)
                .member(member)
                .build();
    }
}
