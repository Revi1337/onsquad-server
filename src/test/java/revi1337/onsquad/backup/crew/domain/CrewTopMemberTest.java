package revi1337.onsquad.backup.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew_member.domain.entity.CrewTopMember;

class CrewTopMemberTest {

    @Test
    @DisplayName("Crew 상위 멤버 생성에 성공한다.")
    void success1() {
        CrewTopMember TOP_MEMBER = new CrewTopMember(
                1L,
                1,
                10,
                1L,
                REVI_NICKNAME_VALUE,
                REVI_MBTI_VALUE,
                LocalDateTime.now()
        );

        assertThat(TOP_MEMBER.getCrewId()).isEqualTo(1L);
        assertThat(TOP_MEMBER.getRanks()).isEqualTo(1);
        assertThat(TOP_MEMBER.getContribute()).isEqualTo(10);
        assertThat(TOP_MEMBER.getMemberId()).isEqualTo(1L);
        assertThat(TOP_MEMBER.getNickname()).isEqualTo(REVI_NICKNAME_VALUE);
        assertThat(TOP_MEMBER.getMbti()).isEqualTo(REVI_MBTI_VALUE);
        assertThat(TOP_MEMBER.getParticipateAt()).isNotNull();
    }

    @Test
    void localDateTest() {
        LocalDate today = LocalDate.now();
        LocalDate previousSunday = today.with(DayOfWeek.SUNDAY).minusWeeks(1);
    }
}
