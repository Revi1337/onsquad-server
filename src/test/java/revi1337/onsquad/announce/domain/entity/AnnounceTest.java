package revi1337.onsquad.announce.domain.entity;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

class AnnounceTest {

    @Test
    @DisplayName("공지사항을 생성하면 입력한 값이 설정되고 상단 고정 상태는 해제되어 있다.")
    void create() {
        Member revi = createRevi(1L);
        Crew crew = createCrew(2L, revi);

        Announce announce = new Announce("title", "content", crew, revi);

        assertSoftly(softly -> {
            softly.assertThat(announce.getTitle().getValue()).isEqualTo("title");
            softly.assertThat(announce.getContent()).isEqualTo("content");
            softly.assertThat(announce.getCrew()).isEqualTo(crew);
            softly.assertThat(announce.getMember()).isEqualTo(revi);
            softly.assertThat(announce.isPinned()).isFalse();
            softly.assertThat(announce.getPinnedAt()).isNull();
        });
    }

    @Test
    @DisplayName("작성자가 있으면 작성자 ID 를, 탈퇴한 작성자면 null 을 반환한다.")
    void getWriterId() {
        Member revi = createRevi(1L);
        Crew crew = createCrew(2L, revi);

        Announce announce = new Announce("title", "content", crew, revi);
        Announce withdrawn = new Announce("title", "content", crew, null);

        assertSoftly(softly -> {
            softly.assertThat(announce.getWriterId()).isEqualTo(1L);
            softly.assertThat(withdrawn.getWriterId()).isNull();
        });
    }

    @Test
    @DisplayName("공지사항의 제목과 내용을 수정한다.")
    void update() {
        Member revi = createRevi(1L);
        Crew crew = createCrew(2L, revi);
        Announce announce = new Announce("title", "content", crew, revi);

        announce.update("update-title", "update-content");

        assertSoftly(softly -> {
            softly.assertThat(announce.getTitle().getValue()).isEqualTo("update-title");
            softly.assertThat(announce.getContent()).isEqualTo("update-content");
        });
    }

    @Test
    @DisplayName("공지사항을 상단 고정하면 고정 상태가 되고 고정 시각이 기록된다.")
    void pin() {
        Member revi = createRevi(1L);
        Crew crew = createCrew(2L, revi);
        Announce announce = new Announce("title", "content", crew, revi);
        LocalDateTime updateTime = LocalDate.of(2026, 1, 4).atStartOfDay();

        announce.pin(updateTime);

        assertSoftly(softly -> {
            softly.assertThat(announce.isPinned()).isTrue();
            softly.assertThat(announce.getPinnedAt()).isEqualTo(updateTime);
        });
    }

    @Test
    @DisplayName("상단 고정된 공지사항을 해제하면 고정 상태와 고정 시각이 초기화된다.")
    void unpin() {
        Member revi = createRevi(1L);
        Crew crew = createCrew(2L, revi);
        Announce announce = new Announce("title", "content", crew, revi);
        LocalDateTime updateTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        announce.pin(updateTime);

        announce.unpin();

        assertSoftly(softly -> {
            softly.assertThat(announce.isPinned()).isFalse();
            softly.assertThat(announce.getPinnedAt()).isNull();
        });
    }
}
