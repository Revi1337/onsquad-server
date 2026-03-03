package revi1337.onsquad.squad_member.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

class SquadMemberJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberRepository;

    @Test
    @DisplayName("스쿼드 ID와 멤버 ID로 특정 스쿼드 멤버를 조회한다.")
    void findBySquadIdAndMemberId() {
        Member member1 = memberRepository.save(createRevi());
        Member member2 = memberRepository.save(createAndong());
        Member member3 = memberRepository.save(createKwangwon());
        Crew crew = createCrew(member1);
        crew.addCrewMember(createManagerCrewMember(crew, member2));
        crew.addCrewMember(createGeneralCrewMember(crew, member3));
        crewRepository.save(crew);
        Squad squad = createSquad(crew, member1);
        squad.addMembers(createGeneralSquadMember(squad, member2));
        squad.addMembers(createGeneralSquadMember(squad, member3));
        squadRepository.save(squad);

        Optional<SquadMember> result = squadMemberRepository.findBySquadIdAndMemberId(squad.getId(), member1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().isLeader()).isTrue();
    }

    @Test
    @DisplayName("멤버 ID를 기준으로 모든 스쿼드 멤버 관계를 삭제한다.")
    void deleteByMemberId() {
        Member member1 = memberRepository.save(createRevi());
        Member member2 = memberRepository.save(createAndong());
        Member member3 = memberRepository.save(createKwangwon());
        Crew crew = createCrew(member1);
        crew.addCrewMember(createManagerCrewMember(crew, member2));
        crew.addCrewMember(createGeneralCrewMember(crew, member3));
        crewRepository.save(crew);
        Squad squad = createSquad(crew, member1);
        squad.addMembers(createGeneralSquadMember(squad, member2));
        squad.addMembers(createGeneralSquadMember(squad, member3));
        squadRepository.save(squad);

        int deleted = squadMemberRepository.deleteByMemberId(member1.getId());

        assertThat(deleted).isEqualTo(1);
    }

    @Test
    @DisplayName("스쿼드 ID 목록에 포함된 모든 스쿼드 멤버 관계를 한꺼번에 삭제한다.")
    void deleteBySquadIdIn() {
        Member member1 = memberRepository.save(createRevi());
        Member member2 = memberRepository.save(createAndong());
        Member member3 = memberRepository.save(createKwangwon());
        Crew crew = createCrew(member1);
        crew.addCrewMember(createManagerCrewMember(crew, member2));
        crew.addCrewMember(createGeneralCrewMember(crew, member3));
        crewRepository.save(crew);
        Squad squad = createSquad(crew, member1);
        squad.addMembers(createGeneralSquadMember(squad, member2));
        squad.addMembers(createGeneralSquadMember(squad, member3));
        squadRepository.save(squad);

        int deleted = squadMemberRepository.deleteBySquadIdIn(List.of(squad.getId()));

        assertThat(deleted).isEqualTo(3);
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
