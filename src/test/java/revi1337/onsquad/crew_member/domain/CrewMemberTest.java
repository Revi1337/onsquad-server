package revi1337.onsquad.crew_member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.Member;

class CrewMemberTest {

    @Test
    @DisplayName("GENERAL CrewMember 생성에 성공한다. (Crew 등록도 같이)")
    void success1() {
        Crew CREW = CREW(REVI());
        Member ANDONG = ANDONG();

        CrewMember CREW_MEMBER = CrewMember.forGeneral(CREW, ANDONG, LocalDateTime.now());

        assertThat(CREW_MEMBER.isGeneral()).isTrue();
        assertThat(CREW_MEMBER.getCrew()).isEqualTo(CREW);
    }

    @Test
    @DisplayName("OWNER CrewMember 생성에 성공한다.")
    void success2() {
        Crew CREW = CREW(REVI());
        Member ANDONG = ANDONG();

        CrewMember CREW_MEMBER = CrewMember.forOwner(CREW, ANDONG, LocalDateTime.now());

        assertThat(CREW_MEMBER.isOwner()).isTrue();
        assertThat(CREW_MEMBER.getCrew()).isEqualTo(CREW);
    }

    @Test
    @DisplayName("CrewMember 의 Crew 등록에 성공한다. (1)")
    void success3() {
        Member ANDONG = ANDONG();
        CrewMember CREW_MEMBER = CrewMember.forGeneral(ANDONG, LocalDateTime.now());
        Crew CREW = CREW(REVI());

        CREW_MEMBER.addCrew(CREW);

        assertThat(CREW_MEMBER.isGeneral()).isTrue();
        assertThat(CREW_MEMBER.getCrew()).isEqualTo(CREW);
    }

    @Test
    @DisplayName("CrewMember 의 Crew 등록에 성공한다. (2)")
    void success4() {
        Member ANDONG = ANDONG();
        CrewMember CREW_MEMBER = CrewMember.forOwner(ANDONG, LocalDateTime.now());
        Crew CREW = CREW(REVI());

        CREW_MEMBER.addCrew(CREW);

        assertThat(CREW_MEMBER.isOwner()).isTrue();
        assertThat(CREW_MEMBER.getCrew()).isEqualTo(CREW);
    }

    @Test
    @DisplayName("Crew 의 OWNER 확인에 성공한다.")
    void success5() {
        Member ANDONG = ANDONG();
        CrewMember CREW_MEMBER = CrewMember.forOwner(ANDONG, LocalDateTime.now());
        Crew CREW = CREW(REVI());

        CREW_MEMBER.addCrew(CREW);

        assertThat(CREW_MEMBER.isOwner()).isTrue();
        assertThat(CREW_MEMBER.isNotOwner()).isFalse();
    }

    @Test
    @DisplayName("Crew 의 GENERAL 확인에 성공한다.")
    void success6() {
        Member ANDONG = ANDONG();
        CrewMember CREW_MEMBER = CrewMember.forGeneral(ANDONG, LocalDateTime.now());
        Crew CREW = CREW(REVI());

        CREW_MEMBER.addCrew(CREW);

        assertThat(CREW_MEMBER.isOwner()).isFalse();
        assertThat(CREW_MEMBER.isNotOwner()).isTrue();
    }

    @Test
    @DisplayName("Crew 해제에 성공한다. (1)")
    void success7() {
        Crew CREW = CREW(REVI());
        Member ANDONG = ANDONG();
        CrewMember CREW_MEMBER = CrewMember.forGeneral(CREW, ANDONG, LocalDateTime.now());

        CREW_MEMBER.releaseCrew();

        assertThat(CREW_MEMBER.getCrew()).isNull();
    }

    @Test
    @DisplayName("Crew 해제에 성공한다. (2)")
    void success8() {
        Crew CREW = CREW(REVI());
        Member ANDONG = ANDONG();
        CrewMember CREW_MEMBER = CrewMember.forOwner(CREW, ANDONG, LocalDateTime.now());

        CREW_MEMBER.releaseCrew();

        assertThat(CREW_MEMBER.getCrew()).isNull();
    }
}
