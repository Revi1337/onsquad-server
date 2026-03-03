package revi1337.onsquad.squad_request.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.event.RequestAccepted;
import revi1337.onsquad.squad_request.domain.event.RequestAdded;
import revi1337.onsquad.squad_request.domain.event.RequestRejected;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestJpaRepository;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

class SquadRequestCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Autowired
    private SquadRequestJpaRepository squadRequestJpaRepository;

    @SpyBean
    private SquadRequestRepository squadRequestRepository;

    @Autowired
    private SquadRequestCommandService squadRequestCommandService;

    @Autowired
    private ApplicationEvents events;

    @Nested
    @DisplayName("스쿼드 참가 신청")
    class request {

        @Test
        @DisplayName("이전에 신청한 내역이 없다면 새로운 참가 신청을 생성하고 이벤트를 발행한다.")
        void success() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewJpaRepository.save(crew);
            Squad squad = squadJpaRepository.save(createSquad(crew, revi));
            clearPersistenceContext();

            squadRequestCommandService.request(andong.getId(), squad.getId());

            assertSoftly(softly -> {
                clearPersistenceContext();
                softly.assertThat(squadRequestJpaRepository.findAll()).hasSize(1);
                softly.assertThat(events.stream(RequestAdded.class).count()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("이미 동일한 스쿼드에 신청한 내역이 있다면 추가 신청을 생성하지 않고 이벤트도 발행하지 않는다.")
        void success2() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Crew crew = crewJpaRepository.save(createCrew(revi));
            Squad squad = squadJpaRepository.save(createSquad(crew, revi));
            squadRequestJpaRepository.save(createSquadRequest(squad, andong));
            clearPersistenceContext();

            squadRequestCommandService.request(andong.getId(), squad.getId());

            assertSoftly(softly -> {
                softly.assertThat(events.stream(RequestAdded.class).count()).isEqualTo(0);
                verify(squadRequestRepository, never()).save(any(SquadRequest.class));
            });
        }
    }

    @Nested
    @DisplayName("스쿼드 참가 신청 수락")
    class acceptRequest {

        @Test
        @DisplayName("스쿼드 리더는 참가 신청을 수락하여 멤버를 추가하고 수락 이벤트를 발행할 수 있다.")
        void success() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Crew crew = crewJpaRepository.save(createCrew(revi));
            Squad squad = squadJpaRepository.save(createSquad(crew, revi));
            SquadRequest request = squadRequestJpaRepository.save(createSquadRequest(squad, andong));
            clearPersistenceContext();

            squadRequestCommandService.acceptRequest(revi.getId(), squad.getId(), request.getId());

            assertSoftly(softly -> {
                clearPersistenceContext();
                softly.assertThat(squadMemberJpaRepository.findAll()).hasSize(2);
                softly.assertThat(squadRequestJpaRepository.findById(request.getId())).isEmpty();
                softly.assertThat(events.stream(RequestAccepted.class).count()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("요청된 스쿼드 ID와 신청 정보 내의 스쿼드 ID가 일치하지 않으면 수락에 실패하고 이벤트가 발행되지 않는다.")
        void fail1() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Crew crew = crewJpaRepository.save(createCrew(revi));
            Squad squad = squadJpaRepository.save(createSquad(crew, revi));
            SquadRequest request = squadRequestJpaRepository.save(createSquadRequest(squad, andong));
            clearPersistenceContext();

            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> squadRequestCommandService.acceptRequest(revi.getId(), 100L, request.getId()));
                softly.assertThat(events.stream(RequestAccepted.class).count()).isEqualTo(0);
            });
        }

        @Test
        @DisplayName("수락을 시도하는 주체가 스쿼드 리더가 아니면 수락에 실패하고 이벤트가 발행되지 않는다.")
        void fail2() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Member kwangwon = memberJpaRepository.save(createKwangwon());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            crewJpaRepository.save(crew);
            Squad squad = createSquad(crew, revi);
            squad.addMembers(createGeneralSquadMember(squad, andong));
            squadJpaRepository.save(squad);
            SquadRequest request = squadRequestJpaRepository.save(createSquadRequest(squad, kwangwon));
            clearPersistenceContext();

            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> squadRequestCommandService.acceptRequest(andong.getId(), squad.getId(), request.getId()));
                softly.assertThat(events.stream(RequestAccepted.class).count()).isEqualTo(0);
            });
        }
    }

    @Nested
    @DisplayName("스쿼드 참가 신청 거절")
    class rejectRequest {

        @Test
        @DisplayName("스쿼드 리더는 참가 신청을 거절하여 내역을 삭제하고 거절 이벤트를 발행할 수 있다.")
        void success() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Crew crew = crewJpaRepository.save(createCrew(revi));
            Squad squad = squadJpaRepository.save(createSquad(crew, revi));
            SquadRequest request = squadRequestJpaRepository.save(createSquadRequest(squad, andong));
            clearPersistenceContext();

            squadRequestCommandService.rejectRequest(revi.getId(), squad.getId(), request.getId());

            assertSoftly(softly -> {
                clearPersistenceContext();
                softly.assertThat(squadMemberJpaRepository.findAll()).hasSize(1);
                softly.assertThat(squadRequestJpaRepository.findById(request.getId())).isEmpty();
                softly.assertThat(events.stream(RequestRejected.class).count()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("요청된 스쿼드 ID와 신청 정보 내의 스쿼드 ID가 일치하지 않으면 거절에 실패하고 이벤트가 발행되지 않는다.")
        void fail1() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Crew crew = crewJpaRepository.save(createCrew(revi));
            Squad squad = squadJpaRepository.save(createSquad(crew, revi));
            SquadRequest request = squadRequestJpaRepository.save(createSquadRequest(squad, andong));
            clearPersistenceContext();

            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> squadRequestCommandService.rejectRequest(revi.getId(), 100L, request.getId()));
                softly.assertThat(events.stream(RequestRejected.class).count()).isEqualTo(0);
            });
        }

        @Test
        @DisplayName("거절을 시도하는 주체가 스쿼드 리더가 아니면 거절에 실패하고 이벤트가 발행되지 않는다.")
        void fail2() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Member kwangwon = memberJpaRepository.save(createKwangwon());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            crewJpaRepository.save(crew);
            Squad squad = createSquad(crew, revi);
            squad.addMembers(createGeneralSquadMember(squad, andong));
            squadJpaRepository.save(squad);
            SquadRequest request = squadRequestJpaRepository.save(createSquadRequest(squad, kwangwon));
            clearPersistenceContext();

            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> squadRequestCommandService.rejectRequest(andong.getId(), squad.getId(), request.getId()));
                softly.assertThat(events.stream(RequestRejected.class).count()).isEqualTo(0);
            });
        }
    }

    @Nested
    @DisplayName("본인의 참가 신청 취소")
    class cancelMyRequest {

        @Test
        @DisplayName("회원 본인이 신청한 내역을 취소하면 데이터가 정상적으로 삭제된다.")
        void success() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Crew crew = crewJpaRepository.save(createCrew(revi));
            Squad squad = squadJpaRepository.save(createSquad(crew, revi));
            SquadRequest request = squadRequestJpaRepository.save(createSquadRequest(squad, andong));
            clearPersistenceContext();

            int deleted = squadRequestCommandService.cancelMyRequest(andong.getId(), squad.getId());

            assertThat(deleted).isEqualTo(1);
        }

        @Test
        @DisplayName("일치하는 신청 내역이 없는 경우 삭제가 수행되지 않는다.")
        void fail() {
            Member revi = memberJpaRepository.save(createRevi());
            Member andong = memberJpaRepository.save(createAndong());
            Crew crew = crewJpaRepository.save(createCrew(revi));
            Squad squad = squadJpaRepository.save(createSquad(crew, revi));
            SquadRequest request = squadRequestJpaRepository.save(createSquadRequest(squad, andong));
            clearPersistenceContext();

            int deleted = squadRequestCommandService.cancelMyRequest(andong.getId(), 100L);

            assertThat(deleted).isEqualTo(0);
        }
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private static CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    private static SquadRequest createSquadRequest(Squad squad, Member member) {
        return SquadRequest.of(squad, member, LocalDateTime.now());
    }
}
