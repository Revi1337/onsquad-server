package revi1337.onsquad.crew_request.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_request.application.response.CrewRequestResponse;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.error.CrewRequestBusinessException;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewRequestQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestRepository;

    @Autowired
    private CrewRequestQueryService crewRequestQueryService;

    @Nested
    class fetchAllRequests {

        @Test
        @DisplayName("manager 이상은 크루 참가신청 목록을 볼 수 있다.")
        void success() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member dummy = memberRepository.save(createMember(1));
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            LocalDateTime dateTime = LocalDate.of(2026, 1, 4).atStartOfDay();
            crewRequestRepository.saveAll(List.of(
                    createCrewRequest(savedCrew, kwangwon, dateTime.plusHours(2)),
                    createCrewRequest(savedCrew, dummy, dateTime.plusHours(3))
            ));
            clearPersistenceContext();
            PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("requestAt").descending());

            PageResponse<CrewRequestResponse> results = crewRequestQueryService.fetchAllRequests(andong.getId(), savedCrew.getId(), pageRequest);

            assertSoftly(softly -> {
                softly.assertThat(results.size()).isEqualTo(2);
                softly.assertThat(results.page()).isEqualTo(1);
                softly.assertThat(results.totalPages()).isEqualTo(1);
                softly.assertThat(results.totalCount()).isEqualTo(2);
                softly.assertThat(results.resultsSize()).isEqualTo(2);

                List<CrewRequestResponse> contents = results.results();
                softly.assertThat(contents.get(0).requester().id()).isEqualTo(dummy.getId());
                softly.assertThat(contents.get(1).requester().id()).isEqualTo(kwangwon.getId());
            });
        }

        @Test
        @DisplayName("manager 미만은 크루 참가신청 목록을 볼 수 없다.")
        void fail() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member dummy = memberRepository.save(createMember(1));
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            LocalDateTime dateTime = LocalDate.of(2026, 1, 4).atStartOfDay();
            crewRequestRepository.saveAll(List.of(
                    createCrewRequest(savedCrew, kwangwon, dateTime.plusHours(2)),
                    createCrewRequest(savedCrew, dummy, dateTime.plusHours(3))
            ));
            clearPersistenceContext();
            PageRequest pageRequest = PageRequest.of(0, 2);

            assertThatThrownBy(() -> crewRequestQueryService
                    .fetchAllRequests(andong.getId(), savedCrew.getId(), pageRequest))
                    .isExactlyInstanceOf(CrewRequestBusinessException.InsufficientAuthority.class);
        }
    }

    private CrewRequest createCrewRequest(Crew crew, Member andong, LocalDateTime dateTime) {
        return CrewRequest.of(crew, andong, dateTime);
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
