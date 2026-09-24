package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.fixture.CrewFixture;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewLeaderboardUpdateServiceTest extends ApplicationLayerTestSupport {

    private static final LocalDateTime WINDOW_FROM = LocalDateTime.of(2026, 1, 5, 0, 0);
    private static final LocalDateTime WINDOW_TO = LocalDateTime.of(2026, 1, 12, 0, 0);

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewRankerRepository crewRankerRepository;

    @Autowired
    private CrewLeaderboardUpdateService leaderboardUpdateService;

    @Test
    @DisplayName("기존 랭킹 데이터를 모두 삭제하고, 집계 기간 내 활동을 기반으로 새로운 순위를 반영한다")
    void refreshLeaderboards() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member joiner = memberJpaRepository.save(createMember(1));
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, joiner, WINDOW_FROM.plusDays(1)));

        // 스케줄러 실행 전 남아있던 지난 주차 랭킹 데이터
        crewRankerRepository.insertBatch(List.of(staleCandidate(crew.getId(), owner)));
        clearPersistenceContext();

        // when
        leaderboardUpdateService.refreshLeaderboards(WINDOW_FROM, WINDOW_TO, 5);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> rankers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(rankers).hasSize(1);
            softly.assertThat(rankers.get(0).getMemberId()).isEqualTo(joiner.getId());
            softly.assertThat(rankers.get(0).getScore()).isEqualTo(5L);
            softly.assertThat(rankers.get(0).getRank()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("rankLimit 을 초과하는 순위의 활동 데이터는 반영되지 않는다")
    void refreshLeaderboards_appliesRankLimit() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member first = memberJpaRepository.save(createMember(1));
        Member second = memberJpaRepository.save(createMember(2));
        Member third = memberJpaRepository.save(createMember(3));
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, first, WINDOW_FROM.plusDays(1)));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, second, WINDOW_FROM.plusDays(2)));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, third, WINDOW_FROM.plusDays(3)));
        clearPersistenceContext();

        // when
        leaderboardUpdateService.refreshLeaderboards(WINDOW_FROM, WINDOW_TO, 2);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> rankers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(rankers).hasSize(2);
            softly.assertThat(rankers).extracting(CrewRanker::getMemberId)
                    .containsExactly(third.getId(), second.getId());
        });
    }

    private CrewRankerCandidate staleCandidate(Long crewId, Member member) {
        return new CrewRankerCandidate(
                crewId,
                1,
                999L,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                LocalDateTime.now()
        );
    }
}
