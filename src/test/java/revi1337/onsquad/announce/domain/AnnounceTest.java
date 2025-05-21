package revi1337.onsquad.announce.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.announce.domain.vo.Title;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;

class AnnounceTest {

    @Test
    @DisplayName("공지사항 생성에 성공한다.")
    void success1() {
        String TITLE = "공지사항 제목";
        String CONTENT = "공지사항 내용";
        Crew CREW = CREW();
        CrewMember CREW_MEMBER = GENERAL_CREW_MEMBER();

        Announce ANNOUNCE = new Announce(TITLE, CONTENT, CREW, CREW_MEMBER);

        assertThat(ANNOUNCE.getTitle()).isEqualTo(new Title(TITLE));
        assertThat(ANNOUNCE.getContent()).isEqualTo(CONTENT);
    }

    @Test
    @DisplayName("공지사항 상단 고정에 성공한다.")
    void success2() {
        String TITLE = "공지사항 제목";
        String CONTENT = "공지사항 내용";
        Crew CREW = CREW();
        CrewMember CREW_MEMBER = GENERAL_CREW_MEMBER();
        Announce ANNOUNCE = new Announce(TITLE, CONTENT, CREW, CREW_MEMBER);
        LocalDateTime NOW = LocalDateTime.now();

        ANNOUNCE.fix(NOW);

        assertThat(ANNOUNCE.isFixed()).isTrue();
        assertThat(ANNOUNCE.getFixedAt()).isEqualTo(NOW);
    }
}
