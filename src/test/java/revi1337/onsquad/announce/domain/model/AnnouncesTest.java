package revi1337.onsquad.announce.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

class AnnouncesTest {

    @Test
    @DisplayName("공지사항 목록에서 중복을 제거한 작성자 ID 리스트를 추출한다.")
    void getWriterIds() {
        Member writer1 = createMember(1L);
        Member writer2 = createMember(2L);
        Crew crew = createCrew(1L, createMember(3L));
        Announces announces = new Announces(List.of(
                createAnnounce(101L, crew, writer1),
                createAnnounce(102L, crew, writer1),
                createAnnounce(103L, crew, writer2)
        ));

        List<Long> writerIds = announces.getWriterIds();

        assertThat(writerIds).hasSize(2).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("내부 리스트를 반환할 때 원본 리스트의 순서를 유지한다.")
    void values() {
        Crew crew = createCrew(1L, createMember(3L));
        Announce announce1 = createAnnounce(101L, crew, createMember(1L));
        Announce announce2 = createAnnounce(102L, crew, createMember(2L));
        Announces announces = new Announces(List.of(announce1, announce2));

        List<Announce> values = announces.values();

        assertThat(values).hasSize(2);
        assertThat(values.get(0).getId()).isEqualTo(101L);
        assertThat(values.get(1).getId()).isEqualTo(102L);
    }

    @Test
    @DisplayName("반환된 리스트를 수정하려고 하면 예외가 발생한다 (불변성 검증).")
    void immutableValues() {
        Crew crew = createCrew(1L, createMember(3L));
        Announces announces = new Announces(new ArrayList<>(List.of(
                createAnnounce(101L, crew, createMember(1L))
        )));

        List<Announce> values = announces.values();
        assertThatThrownBy(() -> values.add(createAnnounce(102L, crew, createMember(2L))))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private Announce createAnnounce(Long id, Crew crew, Member member) {
        Announce announce = new Announce(
                "테스트 제목" + id,
                "테스트 내용" + id,
                crew,
                member
        );
        ReflectionTestUtils.setField(announce, "id", id);
        return announce;
    }
}
