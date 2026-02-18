package revi1337.onsquad.crew.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_UPDATED_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_UPDATED_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_UPDATED_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_UPDATED_NAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.model.CrewCreateSpec;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;

class CrewTest {

    @Test
    @DisplayName("CrewCreateSpec을 통해 크루를 생성하면 방장(Owner) 정보와 초기 인원수가 설정된다.")
    void create() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                CREW_IMAGE_LINK_VALUE
        );

        Crew crew = Crew.create(spec, LocalDateTime.now());

        assertSoftly(softly -> {
            softly.assertThat(crew.getMember()).isNotNull();
            softly.assertThat(crew.getCrewMembers()).hasSize(1);
            softly.assertThat(crew.getName().getValue()).isEqualTo(CREW_NAME_VALUE);
            softly.assertThat(crew.getIntroduce().getValue()).isEqualTo(CREW_INTRODUCE_VALUE);
            softly.assertThat(crew.getDetail().getValue()).isEqualTo(CREW_DETAIL_VALUE);
            softly.assertThat(crew.getCurrentSize()).isEqualTo(1);
            softly.assertThat(crew.getKakaoLink()).isEqualTo(CREW_KAKAO_LINK_VALUE);
            softly.assertThat(crew.getImageUrl()).isEqualTo(CREW_IMAGE_LINK_VALUE);
        });
    }

    @Test
    @DisplayName("새로운 멤버를 크루에 추가하면 크루원 리스트와 현재 인원수가 증가한다.")
    void addCrewMember() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                CREW_IMAGE_LINK_VALUE
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());

        crew.addCrewMember(CrewMemberFactory.general(crew, createAndong(), LocalDateTime.now()));

        assertSoftly(softly -> {
            softly.assertThat(crew.getCrewMembers()).hasSize(2);
            softly.assertThat(crew.getCurrentSize()).isEqualTo(2);
            softly.assertThat(crew.getCurrentSize()).isEqualTo(crew.getCrewMembers().size());
        });
    }

    @Test
    @DisplayName("방장 권한을 다른 멤버에게 위임하면 기존 방장은 일반 멤버가 되고 크루의 대표자가 변경된다.")
    void delegateOwner() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                CREW_IMAGE_LINK_VALUE
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());
        CrewMember currentOwner = findCrewOwner(crew);
        CrewMember nextOwner = createGeneralCrewMember(crew, createAndong(2L));
        crew.addCrewMember(nextOwner);

        crew.delegateOwner(currentOwner, nextOwner);

        assertThat(findCrewOwner(crew).getMember()).isEqualTo(nextOwner.getMember());
    }

    @Test
    @DisplayName("크루의 이름, 소개, 상세 내용 및 오픈채팅 링크를 수정한다.")
    void update() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                CREW_IMAGE_LINK_VALUE
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());

        crew.update(CREW_UPDATED_NAME_VALUE, CREW_UPDATED_INTRODUCE_VALUE, CREW_UPDATED_DETAIL_VALUE, CREW_UPDATED_KAKAO_LINK_VALUE);

        assertSoftly(softly -> {
            softly.assertThat(crew.getName().getValue()).isEqualTo(CREW_UPDATED_NAME_VALUE);
            softly.assertThat(crew.getIntroduce().getValue()).isEqualTo(CREW_UPDATED_INTRODUCE_VALUE);
            softly.assertThat(crew.getDetail().getValue()).isEqualTo(CREW_UPDATED_DETAIL_VALUE);
            softly.assertThat(crew.getKakaoLink()).isEqualTo(CREW_UPDATED_KAKAO_LINK_VALUE);
        });
    }

    @Test
    @DisplayName("크루의 현재 인원수를 수동으로 1명 증가시킨다.")
    void increaseSize() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                CREW_IMAGE_LINK_VALUE
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());

        crew.increaseSize();

        assertThat(crew.getCurrentSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("크루의 현재 인원수를 수동으로 1명 감소시킨다.")
    void decreaseSize() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                CREW_IMAGE_LINK_VALUE
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());

        crew.decreaseSize();

        assertThat(crew.getCurrentSize()).isEqualTo(0);
    }

    @Test
    @DisplayName("새로운 이미지 URL을 입력받아 크루 이미지를 업데이트한다.")
    void updateImage() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                null
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());

        crew.updateImage(CREW_IMAGE_LINK_VALUE);

        assertThat(crew.hasImage()).isTrue();
    }

    @Test
    @DisplayName("크루 이미지를 삭제하면 이미지 URL이 null이 된다.")
    void deleteImage() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                CREW_IMAGE_LINK_VALUE
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());

        crew.deleteImage();

        assertThat(crew.hasImage()).isFalse();
    }

    @Test
    @DisplayName("크루 이미지가 등록되어 있는지 확인한다.")
    void hasImage() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                CREW_IMAGE_LINK_VALUE
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());

        assertThat(crew.hasImage()).isTrue();
    }

    @Test
    @DisplayName("크루 이미지가 비어있는지 확인한다.")
    void hasNotImage() {
        CrewCreateSpec spec = new CrewCreateSpec(
                createRevi(1L),
                CREW_NAME_VALUE,
                CREW_INTRODUCE_VALUE,
                CREW_DETAIL_VALUE,
                CREW_KAKAO_LINK_VALUE,
                null
        );
        Crew crew = Crew.create(spec, LocalDateTime.now());

        assertThat(crew.hasNotImage()).isTrue();
    }

    private CrewMember findCrewOwner(Crew crew) {
        return crew.getCrewMembers().stream()
                .filter(CrewMember::isOwner)
                .findFirst()
                .orElse(null);
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
