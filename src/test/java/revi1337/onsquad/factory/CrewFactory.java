package revi1337.onsquad.factory;

import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;

public class CrewFactory {

    public static final Name NAME = new Name("Crew 명");
    public static final Introduce INTRODUCE = new Introduce("Crew 소개");
    public static final Detail DETAIL = new Detail("Crew 세부정보");
    public static final HashTags HASHTAGS = new HashTags(List.of("해시태그1", "해시태그2", "해시태그3"));
    public static final String KAKAO_LINK = "카카오 오픈채팅 링크";

    public static Crew withName(Name name) {
        return defaultCrew().name(name).build();
    }

    public static Crew withIntroduce(Introduce introduce) {
        return defaultCrew().introduce(introduce).build();
    }

    public static Crew withDetail(Detail detail) {
        return defaultCrew().detail(detail).build();
    }

    public static Crew withHashTags(HashTags hashTags) {
        return defaultCrew().hashTags(hashTags).build();
    }

    public static Crew withKakaoLink(String kakaoLink) {
        return defaultCrew().kakaoLink(kakaoLink).build();
    }

    public static Crew.CrewBuilder defaultCrew() {
        return Crew.builder()
                .name(NAME)
                .introduce(INTRODUCE)
                .detail(DETAIL)
                .hashTags(HASHTAGS)
                .kakaoLink(KAKAO_LINK);
    }
}
