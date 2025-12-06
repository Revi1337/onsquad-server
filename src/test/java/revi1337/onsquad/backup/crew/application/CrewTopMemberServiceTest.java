package revi1337.onsquad.backup.crew.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_ANDONG_RANK2;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_REVI_RANK1;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import revi1337.onsquad.backup.crew.application.dto.Top5CrewMemberDto;
import revi1337.onsquad.backup.crew.domain.repository.CrewTopMemberJpaRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import(CrewTopMemberServiceTest.TestCrewTopMemberInitializer.class)
class CrewTopMemberServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private TestCrewTopMemberInitializer testCrewTopMemberInitializer;

    @Autowired
    private CrewTopMemberService crewTopMemberService;

    @Nested
    @DisplayName("Crew 상위 랭커 조회를 테스트한다.")
    class FindTop5CrewMembers {

        @Test
        @DisplayName("Crew 상위 랭커 조회에 성공한다.")
        void success1() {
            // given
            testCrewTopMemberInitializer.initializedCrewTopMembers();
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            Crew CREW = crewJpaRepository.save(CREW(KWANGWON));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member REVI = memberJpaRepository.save(REVI());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, REVI));

            // when
            List<Top5CrewMemberDto> TOP_MEMBERS = crewTopMemberService.findTop5CrewMembers(REVI.getId(), CREW.getId());

            // then
            assertAll(() -> {
                assertThat(TOP_MEMBERS).hasSize(2);

                assertThat(TOP_MEMBERS.get(0).crewId()).isEqualTo(CREW1_REVI_RANK1.getCrewId());
                assertThat(TOP_MEMBERS.get(0).rank()).isEqualTo(CREW1_REVI_RANK1.getRanks());
                assertThat(TOP_MEMBERS.get(0).contribute()).isEqualTo(CREW1_REVI_RANK1.getContribute());
                assertThat(TOP_MEMBERS.get(0).memberId()).isEqualTo(CREW1_REVI_RANK1.getMemberId());
                assertThat(TOP_MEMBERS.get(0).nickname()).isEqualTo(CREW1_REVI_RANK1.getNickname());
                assertThat(TOP_MEMBERS.get(0).mbti()).isEqualTo(CREW1_REVI_RANK1.getMbti());

                assertThat(TOP_MEMBERS.get(1).crewId()).isEqualTo(CREW1_ANDONG_RANK2.getCrewId());
                assertThat(TOP_MEMBERS.get(1).rank()).isEqualTo(CREW1_ANDONG_RANK2.getRanks());
                assertThat(TOP_MEMBERS.get(1).contribute()).isEqualTo(CREW1_ANDONG_RANK2.getContribute());
                assertThat(TOP_MEMBERS.get(1).memberId()).isEqualTo(CREW1_ANDONG_RANK2.getMemberId());
                assertThat(TOP_MEMBERS.get(1).nickname()).isEqualTo(CREW1_ANDONG_RANK2.getNickname());
                assertThat(TOP_MEMBERS.get(1).mbti()).isEqualTo(CREW1_ANDONG_RANK2.getMbti());
            });
        }

        @Test
        @DisplayName("Crew 에 속하지 않은 사용자는 실패한다.")
        void fail() {
            // given
            testCrewTopMemberInitializer.initializedCrewTopMembers();
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            Crew CREW = crewJpaRepository.save(CREW(KWANGWON));
            Member ANDONG = memberJpaRepository.save(ANDONG());

            assertThatThrownBy(() -> crewTopMemberService.findTop5CrewMembers(ANDONG.getId(), CREW.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
        }
    }

    @Component
    static class TestCrewTopMemberInitializer {

        private static final Logger LOGGER = LoggerFactory.getLogger(TestCrewTopMemberInitializer.class);

        private final CrewTopMemberJpaRepository crewTopMemberJpaRepository;

        public TestCrewTopMemberInitializer(CrewTopMemberJpaRepository crewTopMemberJpaRepository) {
            this.crewTopMemberJpaRepository = crewTopMemberJpaRepository;
        }

        public void initializedCrewTopMembers() {
            LOGGER.info("[Initialize Test Crew Top Members]");
            crewTopMemberJpaRepository.saveAll(List.of(CREW1_REVI_RANK1, CREW1_ANDONG_RANK2));
        }
    }
}
