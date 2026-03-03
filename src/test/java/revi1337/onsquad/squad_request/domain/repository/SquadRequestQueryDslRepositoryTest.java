package revi1337.onsquad.squad_request.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.time.LocalDate;
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
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

@Import(SquadRequestQueryDslRepository.class)
class SquadRequestQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadRequestJpaRepository squadRequestRepository;

    @Autowired
    private SquadRequestJpaRepository squadRequestJpaRepository;

    @Autowired
    private SquadRequestQueryDslRepository squadRequestQueryDslRepository;

    @Test
    @DisplayName("스쿼드 ID로 모든 가입 신청 내역을 조회할 때, 신청자의 간략한 프로필 정보를 포함하여 최신순으로 정렬된 결과를 반환한다.")
    void fetchAllBySquadId() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = crewRepository.save(createCrew(revi));
        Squad squad = squadRepository.save(createSquad(crew, revi));
        LocalDateTime baseTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        SquadRequest request1 = squadRequestRepository.save(createSquadRequest(squad, andong, baseTime.plusHours(2)));
        SquadRequest request2 = squadRequestRepository.save(createSquadRequest(squad, kwangwon, baseTime.plusHours(1)));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 4, Sort.by("requestAt").descending());

        Page<SquadRequest> results = squadRequestQueryDslRepository.fetchAllBySquadId(squad.getId(), pageRequest);

        assertSoftly(softly -> {
            List<SquadRequest> content = results.getContent();
            softly.assertThat(content.get(0).getId()).isEqualTo(request1.getId());
            softly.assertThat(content.get(1).getId()).isEqualTo(request2.getId());
        });
    }

    @Test
    @DisplayName("특정 회원이 보낸 모든 스쿼드 신청 내역을 조회하며, Fetch Join을 통해 연관된 스쿼드, 리더, 크루 정보를 한 번에 가져온다.")
    void fetchMySquadRequestsWithDetails() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createGeneralCrewMember(crew, andong));
        crew.addCrewMember(createGeneralCrewMember(crew, kwangwon));
        crewRepository.save(crew);
        Squad squad1 = squadRepository.save(createSquad(crew, revi));
        Squad squad2 = squadRepository.save(createSquad(crew, andong));
        LocalDateTime baseTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        SquadRequest request1 = createSquadRequest(squad1, kwangwon, baseTime.plusHours(2));
        SquadRequest request2 = createSquadRequest(squad2, kwangwon, baseTime.plusHours(1));
        squadRequestJpaRepository.saveAll(List.of(request1, request2));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 4, Sort.by("requestAt").descending());

        Page<SquadRequest> results = squadRequestQueryDslRepository.fetchMySquadRequestsWithDetails(kwangwon.getId(), pageRequest);

        assertSoftly(softly -> {
            List<SquadRequest> content = results.getContent();
            softly.assertThat(content.get(0).getId()).isEqualTo(request1.getId());
            softly.assertThat(content.get(1).getId()).isEqualTo(request2.getId());
        });
    }

    private static CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    private static SquadRequest createSquadRequest(Squad squad, Member member, LocalDateTime requestAt) {
        return SquadRequest.of(squad, member, requestAt);
    }
}
