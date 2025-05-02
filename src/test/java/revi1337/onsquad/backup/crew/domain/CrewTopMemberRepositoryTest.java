package revi1337.onsquad.backup.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_ANDONG_RANK2;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_KWANGWON_RANK3;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_REVI_RANK1;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.backup.crew.domain.dto.Top5CrewMemberDomainDto;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadJpaRepository;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;

@Import({CrewTopMemberRepositoryImpl.class, CrewTopMemberJdbcRepository.class})
class CrewTopMemberRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private CrewTopMemberJpaRepository crewTopMemberJpaRepository;

    @Autowired
    private CrewTopMemberRepository crewTopMemberRepository;

    @Nested
    @DisplayName("CrewTopMember 가 존재하는지 테스트한다.")
    class Exists {

        @Test
        @DisplayName("CrewTopMember 가 하나라도 존재하면 True 를 반환한다.")
        void success1() {
            crewTopMemberJpaRepository.save(CREW1_REVI_RANK1);

            boolean exists = crewTopMemberRepository.exists();

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("CrewTopMember 가 없으면 False 를 반환한다.")
        void success2() {
            boolean exists = crewTopMemberRepository.exists();

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("CrewTopMember 일괄 Delete 를 테스트한다.")
    class Delete {

        @Test
        @DisplayName("모든 CrewTopMember 삭제에 성공한다.")
        void success1() {
            crewTopMemberJpaRepository.saveAll(List.of(CREW1_REVI_RANK1, CREW1_ANDONG_RANK2));

            crewTopMemberRepository.deleteAllInBatch();

            assertThat(crewTopMemberRepository.exists()).isFalse();
        }
    }

    @Nested
    @DisplayName("CrewTopMember 일괄 Insert 를 테스트한다.")
    class Insert {

        @Test
        @DisplayName("모든 CrewTopMember 삭제에 성공한다.")
        void success1() {
            List<CrewTopMember> TOP_MEMBERS = List.of(CREW1_REVI_RANK1, CREW1_ANDONG_RANK2, CREW1_KWANGWON_RANK3);

            crewTopMemberRepository.batchInsert(TOP_MEMBERS);

            assertThat(crewTopMemberJpaRepository.findAll()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("상위 랭커 추출을 테스트한다.")
    class Fetch {

        @Test
        @DisplayName("상위 랭커 추출에 성공한다.")
        void success1() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            CrewMember ANDONG_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            CrewMember KWANGWON_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, KWANGWON));
            squadJpaRepository.save(Squad.builder() // TODO Squad 를 테스트 할때, Fixture 를 구성하고 리팩토링해야 한다.
                    .crew(CREW)
                    .crewMember(ANDONG_CREW_MEMBER)
                    .title(new Title("스쿼드 타이틀"))
                    .content(new Content("스쿼드 컨텐츠"))
                    .capacity(new Capacity(10))
                    .address(new Address("공백", "공백"))
                    .build());
            squadJpaRepository.save(Squad.builder()
                    .crew(CREW)
                    .crewMember(ANDONG_CREW_MEMBER)
                    .title(new Title("스쿼드 타이틀"))
                    .content(new Content("스쿼드 컨텐츠"))
                    .capacity(new Capacity(10))
                    .address(new Address("공백", "공백"))
                    .build());
            squadJpaRepository.save(Squad.builder()
                    .crew(CREW)
                    .crewMember(KWANGWON_CREW_MEMBER)
                    .title(new Title("스쿼드 타이틀"))
                    .content(new Content("스쿼드 컨텐츠"))
                    .capacity(new Capacity(10))
                    .address(new Address("공백", "공백"))
                    .build());
            LocalDate TO = LocalDate.now();
            LocalDate FROM = TO.minusDays(7);

            // when
            List<Top5CrewMemberDomainDto> TOP_MEMBERS = crewTopMemberRepository.fetchAggregatedTopMembers(TO, FROM, 5);

            // then
            assertAll(() -> {
                assertThat(TOP_MEMBERS).hasSize(2);

                assertThat(TOP_MEMBERS.get(0).memberId()).isEqualTo(ANDONG.getId());
                assertThat(TOP_MEMBERS.get(0).rank()).isEqualTo(1);
                assertThat(TOP_MEMBERS.get(0).contribute()).isEqualTo(2);

                assertThat(TOP_MEMBERS.get(1).memberId()).isEqualTo(KWANGWON.getId());
                assertThat(TOP_MEMBERS.get(1).rank()).isEqualTo(2);
                assertThat(TOP_MEMBERS.get(1).contribute()).isEqualTo(1);
            });
        }
    }
}