package revi1337.onsquad.crew_member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

class CrewMemberServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private CrewMemberService crewMemberService;

    @Nested
    @DisplayName("Crew 에 속한 CrewMember 들 조회를 테스트한다.")
    class FindCrewMembers {

        @Test
        @DisplayName("Crew 에 속한 CrewMember 들 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            LocalDateTime NOW = LocalDateTime.now();
            crewMemberRepository.save(CrewMember.forGeneral(CREW, ANDONG, NOW));
            crewMemberRepository.save(CrewMember.forGeneral(CREW, KWANGWON, NOW.plusMinutes(1)));

            List<CrewMemberDto> DTOS = crewMemberService.fetchCrewMembers(REVI.getId(), CREW.getId());

            assertAll(() -> {
                assertThat(DTOS).hasSize(3);

                assertThat(DTOS.get(0).memberInfo().id()).isEqualTo(KWANGWON.getId());
                assertThat(DTOS.get(0).memberInfo().nickname()).isEqualTo(KWANGWON_NICKNAME_VALUE);
                assertThat(DTOS.get(0).memberInfo().mbti()).isSameAs(KWANGWON_MBTI_VALUE);
                assertThat(DTOS.get(0).participantAt()).isEqualTo(NOW.plusMinutes(1));

                assertThat(DTOS.get(1).memberInfo().id()).isEqualTo(ANDONG.getId());
                assertThat(DTOS.get(1).memberInfo().nickname()).isEqualTo(ANDONG_NICKNAME_VALUE);
                assertThat(DTOS.get(1).memberInfo().mbti()).isSameAs(ANDONG_MBTI_VALUE);
                assertThat(DTOS.get(1).participantAt()).isEqualTo(NOW);

                assertThat(DTOS.get(2).memberInfo().id()).isEqualTo(REVI.getId());
                assertThat(DTOS.get(2).memberInfo().nickname()).isEqualTo(REVI_NICKNAME_VALUE);
                assertThat(DTOS.get(2).memberInfo().mbti()).isSameAs(REVI_MBTI_VALUE);
                assertThat(DTOS.get(2).participantAt()).isNotNull();
            });
        }

        @Test
        @DisplayName("Crew 에 속하지 않은 사용자면 Crew 에 속한 CrewMember 들 조회에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            LocalDateTime NOW = LocalDateTime.now();
            crewMemberRepository.save(CrewMember.forGeneral(CREW, ANDONG, NOW));

            assertThatThrownBy(() -> crewMemberService.fetchCrewMembers(KWANGWON.getId(), CREW.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
        }
    }
}