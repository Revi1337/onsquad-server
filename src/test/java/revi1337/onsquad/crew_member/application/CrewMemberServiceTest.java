package revi1337.onsquad.crew_member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG, NOW));
            crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, KWANGWON, NOW.plusMinutes(1)));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 5);

            Page<CrewMemberDto> DTOS = crewMemberService.fetchCrewMembers(REVI.getId(), CREW.getId(), PAGE_REQUEST);

            assertAll(() -> {
                assertThat(DTOS).hasSize(3);

                assertThat(DTOS.getContent().get(0).member().id()).isEqualTo(KWANGWON.getId());
                assertThat(DTOS.getContent().get(0).member().nickname()).isEqualTo(KWANGWON_NICKNAME_VALUE);
                assertThat(DTOS.getContent().get(0).member().mbti()).isSameAs(KWANGWON_MBTI_VALUE);

                assertThat(DTOS.getContent().get(1).member().id()).isEqualTo(ANDONG.getId());
                assertThat(DTOS.getContent().get(1).member().nickname()).isEqualTo(ANDONG_NICKNAME_VALUE);
                assertThat(DTOS.getContent().get(1).member().mbti()).isSameAs(ANDONG_MBTI_VALUE);

                assertThat(DTOS.getContent().get(2).member().id()).isEqualTo(REVI.getId());
                assertThat(DTOS.getContent().get(2).member().nickname()).isEqualTo(REVI_NICKNAME_VALUE);
                assertThat(DTOS.getContent().get(2).member().mbti()).isSameAs(REVI_MBTI_VALUE);
            });
        }

        @Test
        @DisplayName("Crew 에 속하지 않은 사용자면 Crew 에 속한 CrewMember 들 조회에 실패한다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            LocalDateTime NOW = LocalDateTime.now();
            crewMemberRepository.save(CrewMember.forGeneral(CREW, ANDONG, NOW));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 5);

            assertThatThrownBy(() -> crewMemberService.fetchCrewMembers(KWANGWON.getId(), CREW.getId(), PAGE_REQUEST))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
        }

        @Test
        @DisplayName("Crew 에 속하지 않은 사용자가 Owner 가 아니라면 Crew 에 속한 CrewMember 들 조회에 실패한다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 5);

            assertThatThrownBy(() -> crewMemberService.fetchCrewMembers(ANDONG.getId(), CREW.getId(), PAGE_REQUEST))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotOwner.class);
        }
    }
}
