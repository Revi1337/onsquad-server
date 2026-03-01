package revi1337.onsquad.squad.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;

class SquadJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberRepository;

    @Test
    @DisplayName("스쿼드 ID로 조회 시 크루 정보를 페치 조인하여 함께 가져온다.")
    void findWithCrewById() {
        Member member = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(member));
        Squad squad1 = squadRepository.save(createSquad(crew, member));
        Squad squad2 = squadRepository.save(createSquad(crew, member));
        clearPersistenceContext();

        Optional<Squad> squadOpt = squadRepository.findWithCrewById(squad1.getId());

        assertThat(squadOpt).isPresent();
    }

    @Test
    @DisplayName("특정 멤버가 생성한 모든 스쿼드의 ID 목록을 조회한다.")
    void findIdsByMemberId() {
        Member member = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(member));
        Squad squad1 = squadRepository.save(createSquad(crew, member));
        Squad squad2 = squadRepository.save(createSquad(crew, member));

        List<Long> squadIds = squadRepository.findIdsByMemberId(member.getId());

        assertThat(squadIds).containsExactlyInAnyOrder(squad1.getId(), squad2.getId());
    }

    @Test
    @DisplayName("여러 크루 ID 목록에 속해있는 모든 스쿼드의 ID 목록을 조회한다.")
    void findIdsByCrewIdIn() {
        Member member = memberRepository.save(createRevi());
        Crew crew1 = crewRepository.save(createCrew(member));
        Crew crew2 = crewRepository.save(createCrew(member));
        Squad squad1 = squadRepository.save(createSquad(crew1, member));
        Squad squad2 = squadRepository.save(createSquad(crew2, member));

        List<Long> squadIds = squadRepository.findIdsByCrewIdIn(List.of(crew1.getId(), crew2.getId()));

        assertThat(squadIds).containsExactlyInAnyOrder(squad1.getId(), squad2.getId());
    }

    @Test
    @DisplayName("스쿼드 ID 목록을 전달받아 해당하는 스쿼드들을 벌크 삭제한다.")
    void deleteByIdIn() {
        Member member = memberRepository.save(createRevi());
        Crew crew1 = crewRepository.save(createCrew(member));
        Crew crew2 = crewRepository.save(createCrew(member));
        Squad squad1 = squadRepository.save(createSquad(crew1, member));
        Squad squad2 = squadRepository.save(createSquad(crew2, member));
        squadMemberRepository.deleteAllInBatch();

        int deleted = squadRepository.deleteByIdIn(List.of(squad1.getId(), squad2.getId()));

        assertThat(deleted).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 크루 ID 목록에 속한 모든 스쿼드들을 벌크 삭제한다.")
    void deleteByCrewIdIn() {
        Member member = memberRepository.save(createRevi());
        Crew crew1 = crewRepository.save(createCrew(member));
        Crew crew2 = crewRepository.save(createCrew(member));
        squadRepository.save(createSquad(crew1, member));
        squadRepository.save(createSquad(crew2, member));
        squadMemberRepository.deleteAllInBatch();

        int deleted = squadRepository.deleteByCrewIdIn(List.of(crew1.getId()));

        assertThat(deleted).isEqualTo(1);
    }

    @Test
    @DisplayName("특정 멤버가 속한 모든 스쿼드의 현재 인원을 1 줄이고 남은 자리를 1 늘리는 벌크 업데이트를 수행한다.")
    void decrementCountByMemberId() {
        Member leader = memberRepository.save(createRevi());
        Member targetMember = memberRepository.save(createAndong());
        Crew crew = crewRepository.save(createCrew(leader));
        Squad squad1 = squadRepository.save(createSquad(crew, leader));
        squadMemberRepository.save(createGeneralSquadMember(squad1, targetMember));
        squad1.increaseCurrentSize();
        Squad squad2 = squadRepository.save(createSquad(crew, leader));
        squadMemberRepository.save(createGeneralSquadMember(squad2, targetMember));
        squad2.increaseCurrentSize();
        Squad squad3 = squadRepository.save(createSquad(crew, leader));
        clearPersistenceContext();

        int updatedCount = squadRepository.decrementCountByMemberId(targetMember.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(updatedCount).isEqualTo(2);

            Squad s1 = squadRepository.findById(squad1.getId()).orElseThrow();
            softly.assertThat(s1.getCurrentSize()).isEqualTo(1);
            softly.assertThat(s1.getRemain()).isEqualTo(9);

            Squad s3 = squadRepository.findById(squad3.getId()).orElseThrow();
            softly.assertThat(s3.getCurrentSize()).isEqualTo(1);
            softly.assertThat(s3.getRemain()).isEqualTo(9);
        });
    }
}
