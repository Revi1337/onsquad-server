package revi1337.onsquad.crew_member.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.model.CrewMembership;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import(CrewMemberJdbcRepository.class)
class CrewMemberJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewMemberJdbcRepository crewMemberJdbcRepository;

    @Test
    @DisplayName("후보 멤버십 중 crew_member 테이블에 실제로 존재하는 유효한 멤버십만 반환하고, 탈퇴하여 존재하지 않는 멤버십은 제외한다")
    void findActiveMemberships() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member active1 = memberJpaRepository.save(createAndong());
        Member active2 = memberJpaRepository.save(createKwangwon());
        Member withdrawn = memberJpaRepository.save(createMember(4));
        Crew crew = crewJpaRepository.save(createCrew(owner));

        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, active1, LocalDateTime.now()));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, active2, LocalDateTime.now()));
        clearPersistenceContext();

        Set<CrewMembership> candidates = Set.of(
                new CrewMembership(crew.getId(), active1.getId()),
                new CrewMembership(crew.getId(), active2.getId()),
                new CrewMembership(crew.getId(), withdrawn.getId())
        );

        // when
        Set<CrewMembership> activeMemberships = crewMemberJdbcRepository.findActiveMemberships(candidates);

        // then
        assertSoftly(softly -> {
            softly.assertThat(activeMemberships).hasSize(2);
            softly.assertThat(activeMemberships).containsExactlyInAnyOrder(
                    new CrewMembership(crew.getId(), active1.getId()),
                    new CrewMembership(crew.getId(), active2.getId())
            );
            softly.assertThat(activeMemberships).doesNotContain(new CrewMembership(crew.getId(), withdrawn.getId()));
        });
    }

    @Test
    @DisplayName("서로 다른 크루의 동일 멤버 ID는 (crew_id, member_id) 조합으로 구분되어 정확히 매칭된다")
    void findActiveMemberships_distinguishesByCrewIdAndMemberIdPair() {
        // given
        Member owner1 = memberJpaRepository.save(createRevi());
        Member owner2 = memberJpaRepository.save(createAndong());
        Member sharedMember = memberJpaRepository.save(createKwangwon());
        Crew crew1 = crewJpaRepository.save(createCrew(owner1));
        Crew crew2 = crewJpaRepository.save(createCrew(owner2));

        // sharedMember 는 crew1 에는 소속되어 있지만 crew2 에는 소속되어 있지 않다
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew1, sharedMember, LocalDateTime.now()));
        clearPersistenceContext();

        Set<CrewMembership> candidates = Set.of(
                new CrewMembership(crew1.getId(), sharedMember.getId()),
                new CrewMembership(crew2.getId(), sharedMember.getId())
        );

        // when
        Set<CrewMembership> activeMemberships = crewMemberJdbcRepository.findActiveMemberships(candidates);

        // then
        assertSoftly(softly -> {
            softly.assertThat(activeMemberships).hasSize(1);
            softly.assertThat(activeMemberships).containsExactly(new CrewMembership(crew1.getId(), sharedMember.getId()));
        });
    }

    @Test
    @DisplayName("후보가 비어있으면 쿼리를 수행하지 않고 빈 Set 을 반환한다")
    void findActiveMemberships_returnsEmptySet_whenCandidatesEmpty() {
        // when
        Set<CrewMembership> activeMemberships = crewMemberJdbcRepository.findActiveMemberships(Set.of());

        // then
        assertThat(activeMemberships).isEmpty();
    }
}
