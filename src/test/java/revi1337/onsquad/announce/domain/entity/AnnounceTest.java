package revi1337.onsquad.announce.domain.entity;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

class AnnounceTest {

    @Test
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
