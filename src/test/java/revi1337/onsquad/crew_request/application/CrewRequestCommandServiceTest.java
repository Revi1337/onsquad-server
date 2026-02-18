package revi1337.onsquad.crew_request.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.error.CrewRequestBusinessException;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewRequestCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @SpyBean
    private CrewRequestJpaRepository crewRequestRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Autowired
    private CrewRequestCommandService crewRequestCommandService;

    @Nested
    class request {

        @Test
        @DisplayName("크루 참가신청에 성공한다.")
        void success() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = crewRepository.save(createCrew(revi));
            clearPersistenceContext();

            crewRequestCommandService.request(andong.getId(), crew.getId());

            clearPersistenceContext();
            assertThat(crewRequestRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("이미 참가신청을 했다면 신청되지 않는다.")
        void fail() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = crewRepository.save(createCrew(revi));
            crewRequestRepository.save(createCrewRequest(crew, andong));
            clearPersistenceContext();
            reset(crewRequestRepository);

            crewRequestCommandService.request(andong.getId(), crew.getId());

            clearPersistenceContext();
            verify(crewRequestRepository, never()).save(any(CrewRequest.class));
        }
    }

    @Nested
    class accept {

        @Test
        @DisplayName("참가 신청 수락에 성공한다.")
        void success() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = crewRepository.save(createCrew(revi));
            CrewRequest request = crewRequestRepository.save(createCrewRequest(crew, andong));
            clearPersistenceContext();

            crewRequestCommandService.acceptRequest(revi.getId(), crew.getId(), request.getId());

            clearPersistenceContext();
            assertThat(crewRequestRepository.findAll().size()).isZero();
            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), andong.getId())).isPresent();
        }

        @Test
        @DisplayName("권한 없는 사용자는 참가 신청을 수락할 수 없다.")
        void fail1() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Member kwangwon = memberRepository.save(createKwangwon());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            CrewRequest request = crewRequestRepository.save(createCrewRequest(crew, kwangwon));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewRequestCommandService
                    .acceptRequest(andong.getId(), crew.getId(), request.getId()))
                    .isExactlyInstanceOf(CrewRequestBusinessException.InsufficientAuthority.class);
        }

        @Test
        @DisplayName("참가신청의 크루 정보가 일치하지 않으면 수락할 수 없다.")
        void fail2() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Member kwangwon = memberRepository.save(createKwangwon());
            Crew crew1 = createCrew(revi);
            crew1.addCrewMember(createGeneralCrewMember(crew1, andong));
            crewRepository.save(crew1);
            Crew crew2 = crewRepository.save(createCrew(andong));
            CrewRequest request = crewRequestRepository.save(createCrewRequest(crew1, kwangwon));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewRequestCommandService
                    .acceptRequest(andong.getId(), crew2.getId(), request.getId()))
                    .isExactlyInstanceOf(CrewRequestBusinessException.MismatchReference.class);
        }
    }

    @Nested
    class rejectRequest {

        @Test
        @DisplayName("참가 신청 거절에 성공한다.")
        void success() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = crewRepository.save(createCrew(revi));
            CrewRequest request = crewRequestRepository.save(createCrewRequest(crew, andong));
            clearPersistenceContext();

            crewRequestCommandService.rejectRequest(revi.getId(), crew.getId(), request.getId());

            clearPersistenceContext();
            assertThat(crewRequestRepository.findAll().size()).isZero();
            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), andong.getId())).isEmpty();
        }

        @Test
        @DisplayName("권한 없는 사용자는 참가 신청을 거절할 수 없다.")
        void fail1() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Member kwangwon = memberRepository.save(createKwangwon());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            CrewRequest request = crewRequestRepository.save(createCrewRequest(crew, kwangwon));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewRequestCommandService
                    .rejectRequest(andong.getId(), crew.getId(), request.getId()))
                    .isExactlyInstanceOf(CrewRequestBusinessException.InsufficientAuthority.class);
        }

        @Test
        @DisplayName("참가신청의 크루 정보가 일치하지 않으면 거절할 수 없다.")
        void fail2() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Member kwangwon = memberRepository.save(createKwangwon());
            Crew crew1 = createCrew(revi);
            crew1.addCrewMember(createGeneralCrewMember(crew1, andong));
            crewRepository.save(crew1);
            Crew crew2 = crewRepository.save(createCrew(andong));
            CrewRequest request = crewRequestRepository.save(createCrewRequest(crew1, kwangwon));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewRequestCommandService
                    .rejectRequest(andong.getId(), crew2.getId(), request.getId()))
                    .isExactlyInstanceOf(CrewRequestBusinessException.MismatchReference.class);
        }
    }

    private CrewRequest createCrewRequest(Crew crew, Member andong) {
        return CrewRequest.of(crew, andong, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
