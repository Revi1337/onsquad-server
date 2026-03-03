package revi1337.onsquad.squad_member.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import revi1337.onsquad.squad_member.domain.model.MyParticipantSquad;

@Import(SquadMemberQueryDslRepository.class)
class SquadMemberQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadMemberQueryDslRepository squadMemberQueryDslRepository;

    @Test
    @DisplayName("스쿼드 ID를 통해 해당 스쿼드에 참여 중인 모든 멤버 목록을 fetch join으로 조회한다.")
    void fetchParticipantsBySquadId() {
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
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("participateAt").descending());

        Page<SquadMember> results = squadMemberQueryDslRepository.fetchParticipantsBySquadId(squad.getId(), pageRequest);

        assertThat(results).hasSize(3);
    }

    @Test
    @DisplayName("특정 멤버가 참여 중인 스쿼드 목록을 조회하며, 해당 멤버의 리더 여부와 함께 최근 참여순으로 정렬하여 반환한다.")
    void fetchParticipantSquads() {
        Member member1 = memberRepository.save(createRevi());
        Member member2 = memberRepository.save(createAndong());
        Member member3 = memberRepository.save(createKwangwon());
        Crew crew1 = createCrew(member1);
        crew1.addCrewMember(createManagerCrewMember(crew1, member2));
        crew1.addCrewMember(createGeneralCrewMember(crew1, member3));
        Crew crew2 = createCrew(member2);
        crew2.addCrewMember(createManagerCrewMember(crew2, member1));
        crew2.addCrewMember(createGeneralCrewMember(crew2, member3));
        Crew crew3 = createCrew(member3);
        crew3.addCrewMember(createManagerCrewMember(crew3, member1));
        crew3.addCrewMember(createGeneralCrewMember(crew3, member2));
        crewRepository.saveAll(List.of(crew1, crew2, crew3));
        Squad squad1 = createSquad(crew1, member1);
        squad1.addMembers(createGeneralSquadMember(squad1, member2));
        squad1.addMembers(createGeneralSquadMember(squad1, member3));
        Squad squad2 = createSquad(crew1, member2);
        squad2.addMembers(createGeneralSquadMember(squad2, member1));
        squad2.addMembers(createGeneralSquadMember(squad2, member3));
        Squad squad3 = createSquad(crew1, member3);
        squad3.addMembers(createGeneralSquadMember(squad3, member1));
        squad3.addMembers(createGeneralSquadMember(squad3, member2));
        squadRepository.saveAll(List.of(squad1, squad2, squad3));

        List<MyParticipantSquad> results = squadMemberQueryDslRepository.fetchParticipantSquads(member2.getId());

        assertSoftly(softly -> {
            softly.assertThat(results).hasSize(3);
            softly.assertThat(results.get(0).isLeader()).isFalse();
            softly.assertThat(results.get(0).squad().id()).isEqualTo(squad3.getId());
            softly.assertThat(results.get(1).isLeader()).isTrue();
            softly.assertThat(results.get(1).squad().id()).isEqualTo(squad2.getId());
            softly.assertThat(results.get(2).isLeader()).isFalse();
            softly.assertThat(results.get(2).squad().id()).isEqualTo(squad1.getId());
        });
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
