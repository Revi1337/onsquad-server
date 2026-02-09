package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.model.CrewCreateSpec;
import revi1337.onsquad.member.domain.entity.Member;

public class CrewFixture {

    public static final String CREW_NAME_VALUE = "크루 이름";
    public static final String CREW_INTRODUCE_VALUE = "크루 한줄 소개";
    public static final String CREW_DETAIL_VALUE = "크루 상세 정보";
    public static final String CREW_IMAGE_LINK_VALUE = "https://크루_이미지_링크.com";
    public static final String CREW_KAKAO_LINK_VALUE = "https://크루_카카오_링크.com";

    public static final String CREW_UPDATED_NAME_VALUE = "변경된 크루 이름";
    public static final String CREW_UPDATED_INTRODUCE_VALUE = "변경된 크루 한줄 소개";
    public static final String CREW_UPDATED_DETAIL_VALUE = "변경된 크루 상세 정보";
    public static final String CREW_UPDATED_IMAGE_LINK_VALUE = "https://변경된_크루_이미지_링크.com";
    public static final String CREW_UPDATED_KAKAO_LINK_VALUE = "https://변경된_크루_카카오_링크.com";

    public static Crew createCrew(Member member) {
        return Crew.create(
                new CrewCreateSpec(
                        member,
                        UUID.randomUUID().toString().substring(0, 14),
                        CREW_INTRODUCE_VALUE,
                        CREW_DETAIL_VALUE,
                        CREW_KAKAO_LINK_VALUE,
                        CREW_UPDATED_IMAGE_LINK_VALUE
                ),
                LocalDateTime.now()
        );
    }

    public static Crew createCrew(Member member, LocalDateTime ownerParticipateAt) {
        return Crew.create(
                new CrewCreateSpec(
                        member,
                        UUID.randomUUID().toString().substring(0, 14),
                        CREW_INTRODUCE_VALUE,
                        CREW_DETAIL_VALUE,
                        CREW_KAKAO_LINK_VALUE,
                        CREW_UPDATED_IMAGE_LINK_VALUE
                ),
                ownerParticipateAt
        );
    }

    public static Crew createCrew(String name, Member member) {
        return Crew.create(
                new CrewCreateSpec(
                        member,
                        name,
                        CREW_INTRODUCE_VALUE,
                        CREW_DETAIL_VALUE,
                        CREW_KAKAO_LINK_VALUE,
                        CREW_UPDATED_IMAGE_LINK_VALUE
                ),
                LocalDateTime.now()
        );
    }

    public static Crew createCrew(Long id, Member member) {
        Crew crew = Crew.create(
                new CrewCreateSpec(
                        member,
                        CREW_NAME_VALUE + id,
                        CREW_INTRODUCE_VALUE + id,
                        CREW_DETAIL_VALUE + id,
                        CREW_KAKAO_LINK_VALUE + id,
                        null
                ),
                LocalDateTime.now()
        );
        ReflectionTestUtils.setField(crew, "id", id);
        return crew;
    }

    public static Crew createCrew(Long id, String imageUrl, Member member) {
        Crew crew = Crew.create(
                new CrewCreateSpec(
                        member,
                        CREW_NAME_VALUE + id,
                        CREW_INTRODUCE_VALUE + id,
                        CREW_DETAIL_VALUE + id,
                        CREW_KAKAO_LINK_VALUE + id,
                        imageUrl + id
                ),
                LocalDateTime.now()
        );
        ReflectionTestUtils.setField(crew, "id", id);
        return crew;
    }
}
