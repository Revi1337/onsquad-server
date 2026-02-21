package revi1337.onsquad.crew_member.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
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
import revi1337.onsquad.crew.domain.error.CrewBusinessException;
import revi1337.onsquad.crew.domain.error.CrewErrorCode;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.application.response.CrewMemberResponse;
import revi1337.onsquad.crew_member.application.response.MyParticipantCrewResponse;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

class CrewMemberQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewMemberQueryService crewMemberQueryService;

    @Nested
    @DisplayName("크루원 조회 테스트")
    class fetchParticipants {

        @Test
        @DisplayName("자신이 크루 owner 이면 성공 (isMe & canKick & canDelegateOwner 검사)")
        void test1() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            LocalDateTime ownerParticipateAt = LocalDate.of(2026, 1, 4).atStartOfDay();
            Crew crew = createCrew(revi, ownerParticipateAt);
            crew.addCrewMember(createManagerCrewMember(crew, kwangwon, ownerParticipateAt.plusDays(1)));
            crew.addCrewMember(createGeneralCrewMember(crew, andong, ownerParticipateAt.plusDays(2)));
            crewRepository.save(crew);
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "participateAt"));

            PageResponse<CrewMemberResponse> response = crewMemberQueryService.fetchParticipants(revi.getId(), crew.getId(), pageRequest);

            assertSoftly(softly -> {
                softly.assertThat(response.size()).isEqualTo(3);
                softly.assertThat(response.page()).isEqualTo(1);
                softly.assertThat(response.totalPages()).isEqualTo(1);
                softly.assertThat(response.totalCount()).isEqualTo(3);
                softly.assertThat(response.resultsSize()).isEqualTo(3);

                List<CrewMemberResponse> results = response.results();
                softly.assertThat(results.get(0).states().isMe()).isFalse();
                softly.assertThat(results.get(0).states().canKick()).isTrue();
                softly.assertThat(results.get(0).states().canDelegateOwner()).isTrue();
                softly.assertThat(results.get(0).member().id()).isEqualTo(andong.getId());
                softly.assertThat(results.get(1).states().isMe()).isFalse();
                softly.assertThat(results.get(1).states().canKick()).isTrue();
                softly.assertThat(results.get(1).states().canDelegateOwner()).isTrue();
                softly.assertThat(results.get(1).member().id()).isEqualTo(kwangwon.getId());
                softly.assertThat(results.get(2).states().isMe()).isTrue();
                softly.assertThat(results.get(2).states().canKick()).isFalse();
                softly.assertThat(results.get(2).states().canDelegateOwner()).isFalse();
                softly.assertThat(results.get(2).member().id()).isEqualTo(revi.getId());
            });
        }

        @Test
        @DisplayName("자신이 크루 manager 이면 성공 (isMe & canKick & canDelegateOwner 검사)")
        void test2() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            LocalDateTime ownerParticipateAt = LocalDate.of(2026, 1, 4).atStartOfDay();
            Crew crew = createCrew(revi, ownerParticipateAt);
            crew.addCrewMember(createManagerCrewMember(crew, kwangwon, ownerParticipateAt.plusDays(1)));
            crew.addCrewMember(createGeneralCrewMember(crew, andong, ownerParticipateAt.plusDays(2)));
            crewRepository.save(crew);
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "participateAt"));

            PageResponse<CrewMemberResponse> response = crewMemberQueryService.fetchParticipants(kwangwon.getId(), crew.getId(), pageRequest);

            assertSoftly(softly -> {
                softly.assertThat(response.size()).isEqualTo(3);
                softly.assertThat(response.page()).isEqualTo(1);
                softly.assertThat(response.totalPages()).isEqualTo(1);
                softly.assertThat(response.totalCount()).isEqualTo(3);
                softly.assertThat(response.resultsSize()).isEqualTo(3);

                List<CrewMemberResponse> results = response.results();
                softly.assertThat(results.get(0).states().isMe()).isFalse();
                softly.assertThat(results.get(0).states().canKick()).isTrue();
                softly.assertThat(results.get(0).states().canDelegateOwner()).isFalse();
                softly.assertThat(results.get(0).member().id()).isEqualTo(andong.getId());

                softly.assertThat(results.get(1).states().isMe()).isTrue();
                softly.assertThat(results.get(1).states().canKick()).isFalse();
                softly.assertThat(results.get(1).states().canDelegateOwner()).isFalse();
                softly.assertThat(results.get(1).member().id()).isEqualTo(kwangwon.getId());

                softly.assertThat(results.get(2).states().isMe()).isFalse();
                softly.assertThat(results.get(2).states().canKick()).isFalse();
                softly.assertThat(results.get(2).states().canDelegateOwner()).isFalse();
                softly.assertThat(results.get(2).member().id()).isEqualTo(revi.getId());
            });
        }

        @Test
        @DisplayName("자신이 크루 general 이면 실패")
        void test3() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            LocalDateTime ownerParticipateAt = LocalDate.of(2026, 1, 4).atStartOfDay();
            Crew crew = createCrew(revi, ownerParticipateAt);
            crew.addCrewMember(createManagerCrewMember(crew, kwangwon, ownerParticipateAt.plusDays(1)));
            crew.addCrewMember(createGeneralCrewMember(crew, andong, ownerParticipateAt.plusDays(2)));
            crewRepository.save(crew);
            PageRequest pageRequest = PageRequest.of(0, 3);

            assertThatThrownBy(() -> crewMemberQueryService
                    .fetchParticipants(andong.getId(), crew.getId(), pageRequest))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("내가 참여 중인 크루 테스트")
    class fetchMyParticipatingCrews {

        @Test
        void test() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            LocalDateTime ownerParticipateAt = LocalDate.of(2026, 1, 4).atStartOfDay();
            Crew crew1 = createCrew(revi, ownerParticipateAt);
            Crew crew2 = createCrew(kwangwon, ownerParticipateAt);
            Crew crew3 = createCrew(andong, ownerParticipateAt);
            crew2.addCrewMember(createManagerCrewMember(crew2, revi, ownerParticipateAt.plusDays(1)));
            crew3.addCrewMember(createGeneralCrewMember(crew3, revi, ownerParticipateAt.plusDays(2)));
            crewRepository.saveAll(List.of(crew1, crew2, crew3));
            PageRequest pageRequest = PageRequest.of(0, 3);

            PageResponse<MyParticipantCrewResponse> response = crewMemberQueryService.fetchMyParticipatingCrews(revi.getId(), pageRequest);

            assertSoftly(softly -> {
                softly.assertThat(response.size()).isEqualTo(3);
                softly.assertThat(response.page()).isEqualTo(1);
                softly.assertThat(response.totalPages()).isEqualTo(1);
                softly.assertThat(response.totalCount()).isEqualTo(3);
                softly.assertThat(response.resultsSize()).isEqualTo(3);

                List<MyParticipantCrewResponse> results = response.results();
                softly.assertThat(results.get(0).states().isOwner()).isTrue();
                softly.assertThat(results.get(0).crew().owner().id()).isEqualTo(revi.getId());
                softly.assertThat(results.get(1).states().isOwner()).isFalse();
                softly.assertThat(results.get(1).crew().owner().id()).isEqualTo(andong.getId());
                softly.assertThat(results.get(2).states().isOwner()).isFalse();
                softly.assertThat(results.get(2).crew().owner().id()).isEqualTo(kwangwon.getId());
            });
        }
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member, LocalDateTime participantAt) {
        return CrewMemberFactory.manager(crew, member, participantAt);
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member, LocalDateTime participantAt) {
        return CrewMemberFactory.general(crew, member, participantAt);
    }
}
