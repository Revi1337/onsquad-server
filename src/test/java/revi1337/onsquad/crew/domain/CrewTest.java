package revi1337.onsquad.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI_WITH_ID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.Member;

class CrewTest {

    @Nested
    @DisplayName("Crew 생성을 테스트한다.")
    class CreateCrew {

        @Test
        @DisplayName("기본 Crew 를 생성한다.")
        void success() {
            Crew crew = Crew.of("크루 이름", "크루 한줄 소개", "크루 상세 정보", null, null);

            assertAll(() -> {
                assertThat(crew.getName()).isEqualTo(new Name("크루 이름"));
                assertThat(crew.getIntroduce()).isEqualTo(new Introduce("크루 한줄 소개"));
                assertThat(crew.getDetail()).isEqualTo(new Detail("크루 상세 정보"));
                assertThat(crew.getKakaoLink()).isNull();
                assertThat(crew.getImageUrl()).isNull();
            });
        }

        @Test
        @DisplayName("작성자와 함께 Crew 를 생성한다.")
        void success2() {
            Member revi = REVI_WITH_ID(1L);

            Crew crew = Crew.create(revi, "크루 이름", "크루 한줄 소개", "크루 상세 정보", null, null);

            assertAll(() -> {
                assertThat(crew.isCreatedBy(revi.getId())).isTrue();
                assertThat(crew.countMembers()).isEqualTo(1);

                assertThat(crew.getName()).isEqualTo(new Name("크루 이름"));
                assertThat(crew.getIntroduce()).isEqualTo(new Introduce("크루 한줄 소개"));
                assertThat(crew.getDetail()).isEqualTo(new Detail("크루 상세 정보"));
                assertThat(crew.getKakaoLink()).isNull();
                assertThat(crew.getImageUrl()).isNull();
            });
        }
    }

    @Test
    @DisplayName("기본 Crew 는 이미지가 없다.")
    void success3() {
        Crew crew = Crew.of("크루 이름", "크루 한줄 소개", "크루 상세 정보", null, null);

        assertAll(() -> {
            assertThat(crew.getName()).isEqualTo(new Name("크루 이름"));
            assertThat(crew.getIntroduce()).isEqualTo(new Introduce("크루 한줄 소개"));
            assertThat(crew.getDetail()).isEqualTo(new Detail("크루 상세 정보"));
            assertThat(crew.getKakaoLink()).isNull();
            assertThat(crew.getImageUrl()).isNull();

            assertThat(crew.hasNotImage()).isTrue();
            assertThat(crew.hasImage()).isFalse();
        });
    }

    @Test
    @DisplayName("Crew 이미지 업데이트에 성공한다.")
    void updateCrew() {
        String crewImage = "https://crew_image_1.png";
        Crew crew = Crew.of("크루 이름", "크루 한줄 소개", "크루 상세 정보", null, crewImage);

        crew.updateImage(crewImage);

        assertAll(() -> {
            assertThat(crew.hasImage()).isTrue();
            assertThat(crew.hasNotImage()).isFalse();
        });
    }

    @Test
    @DisplayName("Crew 이미지 삭제에 성공한다.")
    void deleteCrew() {
        String crewImage = "https://crew_image_1.png";
        Crew crew = Crew.of("크루 이름", "크루 한줄 소개", "크루 상세 정보", null, crewImage);
        crew.updateImage(crewImage);

        crew.deleteImage();

        assertAll(() -> {
            assertThat(crew.hasImage()).isFalse();
            assertThat(crew.hasNotImage()).isTrue();
        });
    }
}