package revi1337.onsquad.squad_request.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.time.LocalDateTime;
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
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

class SquadRequestJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadRequestJpaRepository squadRequestRepository;

    @Test
    @DisplayName("스쿼드 ID와 멤버 ID로 특정 신청 내역을 조회한다.")
    void findBySquadIdAndMemberId() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        Squad squad = squadRepository.save(createSquad(crew, revi));
        Member andong = memberRepository.save(createAndong());
        squadRequestRepository.save(createSquadRequest(squad, andong));
        clearPersistenceContext();

        Optional<SquadRequest> requestOpt = squadRequestRepository.findBySquadIdAndMemberId(squad.getId(), andong.getId());

        assertThat(requestOpt).isPresent();
    }

    @Test
    @DisplayName("멤버 ID를 기준으로 해당 멤버가 보낸 모든 스쿼드 신청 내역을 삭제한다.")
    void deleteByMemberId() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Crew crew = crewRepository.save(createCrew(revi));
        Squad squad1 = squadRepository.save(createSquad(1L, crew, revi));
        Squad squad2 = squadRepository.save(createSquad(2L, crew, revi));
        squadRequestRepository.saveAll(List.of(
                createSquadRequest(squad1, andong),
                createSquadRequest(squad2, andong)
        ));
        clearPersistenceContext();

        int deleted = squadRequestRepository.deleteByMemberId(andong.getId());

        assertThat(deleted).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 스쿼드에서 특정 멤버가 보낸 신청 내역을 삭제한다.")
    void deleteBySquadIdAndMemberId() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Crew crew = crewRepository.save(createCrew(revi));
        Squad squad = squadRepository.save(createSquad(crew, revi));
        squadRequestRepository.save(createSquadRequest(squad, andong));
        clearPersistenceContext();

        int deleted = squadRequestRepository.deleteBySquadIdAndMemberId(squad.getId(), andong.getId());

        assertThat(deleted).isEqualTo(1);
    }

    @Test
    @DisplayName("스쿼드 ID 목록에 포함된 모든 신청 내역을 한꺼번에 삭제한다.")
    void deleteBySquadIdIn() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = crewRepository.save(createCrew(revi));
        Squad squad1 = squadRepository.save(createSquad(1L, crew, revi));
        Squad squad2 = squadRepository.save(createSquad(2L, crew, revi));

        squadRequestRepository.saveAll(List.of(createSquadRequest(squad1, andong), createSquadRequest(squad1, kwangwon)));
        squadRequestRepository.saveAll(List.of(createSquadRequest(squad2, andong), createSquadRequest(squad2, kwangwon)));
        clearPersistenceContext();

        int deleted = squadRequestRepository.deleteBySquadIdIn(List.of(squad1.getId(), squad2.getId()));

        assertThat(deleted).isEqualTo(4);
    }

    private static SquadRequest createSquadRequest(Squad squad, Member member) {
        return SquadRequest.of(squad, member, LocalDateTime.now());
    }
}
