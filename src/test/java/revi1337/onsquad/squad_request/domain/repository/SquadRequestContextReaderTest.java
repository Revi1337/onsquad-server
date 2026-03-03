package revi1337.onsquad.squad_request.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestAcceptedContext;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestAddedContext;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestRejectedContext;

@Import(SquadRequestContextReader.class)
class SquadRequestContextReaderTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadRequestContextReader squadRequestContextReader;

    @Test
    @DisplayName("스쿼드 신청 추가 시, 알림에 필요한 크루, 스쿼드, 신청자 및 리더의 정보가 포함된 컨텍스트를 조회한다.")
    void readAddedContext() {
        Member leader = memberRepository.save(createMember("리더"));
        Member requester = memberRepository.save(createMember("신청자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        Long requestId = 100L;
        clearPersistenceContext();

        Optional<RequestAddedContext> contextOpt = squadRequestContextReader.readAddedContext(squad.getId(), requester.getId(), requestId);

        assertSoftly(softly -> {
            assertThat(contextOpt).isPresent();
            RequestAddedContext context = contextOpt.get();
            softly.assertThat(context.crewId()).isEqualTo(crew.getId());
            softly.assertThat(context.crewName()).isEqualTo("우리 크루");
            softly.assertThat(context.squadId()).isEqualTo(squad.getId());
            softly.assertThat(context.squadTitle()).isEqualTo("신규 스쿼드");
            softly.assertThat(context.squadMemberId()).isEqualTo(leader.getId());
            softly.assertThat(context.requestId()).isEqualTo(requestId);
            softly.assertThat(context.requesterId()).isEqualTo(requester.getId());
            softly.assertThat(context.requesterNickname()).isEqualTo("신청자");
        });
    }

    @Test
    @DisplayName("스쿼드 신청 수락 시, 수락 알림 전송에 필요한 크루 정보와 신청자 정보를 포함한 컨텍스트를 조회한다.")
    void readAcceptedContext() {
        Member leader = memberRepository.save(createMember("리더"));
        Member requester = memberRepository.save(createMember("신청자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        Long accepterId = leader.getId();
        clearPersistenceContext();

        Optional<RequestAcceptedContext> contextOpt = squadRequestContextReader.readAcceptedContext(squad.getId(), accepterId, requester.getId());

        assertSoftly(softly -> {
            assertThat(contextOpt).isPresent();
            RequestAcceptedContext context = contextOpt.get();
            softly.assertThat(context.crewId()).isEqualTo(crew.getId());
            softly.assertThat(context.squadTitle()).isEqualTo("신규 스쿼드");
            softly.assertThat(context.accepterId()).isEqualTo(accepterId);
            softly.assertThat(context.requesterId()).isEqualTo(requester.getId());
            softly.assertThat(context.requesterNickname()).isEqualTo("신청자");
        });
    }

    @Test
    @DisplayName("스쿼드 신청 거절 시, 거절 알림에 필요한 크루 및 스쿼드 컨텍스트를 정확하게 조회한다.")
    void readRejectedContext() {
        Member leader = memberRepository.save(createMember("리더"));
        Member requester = memberRepository.save(createMember("신청자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        Long rejecterId = leader.getId();
        clearPersistenceContext();

        Optional<RequestRejectedContext> contextOpt = squadRequestContextReader.readRejectedContext(squad.getId(), rejecterId, requester.getId());

        assertSoftly(softly -> {
            assertThat(contextOpt).isPresent();
            RequestRejectedContext context = contextOpt.get();
            softly.assertThat(context.crewId()).isEqualTo(crew.getId());
            softly.assertThat(context.squadId()).isEqualTo(squad.getId());
            softly.assertThat(context.rejecterId()).isEqualTo(rejecterId);
            softly.assertThat(context.requesterId()).isEqualTo(requester.getId());
            softly.assertThat(context.requesterNickname()).isEqualTo("신청자");
        });
    }
}
