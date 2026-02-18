package revi1337.onsquad.crew_request.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.error.CrewRequestBusinessException;
import revi1337.onsquad.crew_request.domain.error.CrewRequestErrorCode;
import revi1337.onsquad.member.domain.entity.Member;

class CrewRequestPolicyTest {

    @Nested
    class ensureMatchCrew {

        @ParameterizedTest
        @MethodSource("ensureMatchCrewMethodSource")
        @DisplayName("크루 신청정보의 소속 크루 ID 일치 여부를 검증한다")
        void test(CrewRequest request, Long crewId, boolean isMatch) {
            if (isMatch) {
                assertThatCode(() -> CrewRequestPolicy.ensureMatchCrew(request, crewId))
                        .doesNotThrowAnyException();
            } else {
                assertThatThrownBy(() -> CrewRequestPolicy.ensureMatchCrew(request, crewId))
                        .isExactlyInstanceOf(CrewRequestBusinessException.MismatchReference.class)
                        .hasMessage(CrewRequestErrorCode.MISMATCH_CREW_REFERENCE.getDescription());
            }
        }

        private static Stream<Arguments> ensureMatchCrewMethodSource() {
            Member revi = createRevi(1L);
            Crew crew = createCrew(2L, revi);
            CrewRequest request = createCrewRequest(crew, revi);

            return Stream.of(
                    Arguments.of(request, crew.getId(), true),
                    Arguments.of(request, 999L, false)
            );
        }
    }

    @Nested
    class ensureReadRequests {

        @ParameterizedTest
        @MethodSource("ensureReadRequestsMethodSource")
        @DisplayName("참여 신청 목록 조회는 owner 와 manager 만 가능하다.")
        void test(CrewMember crewMember, boolean isSuccess) {
            if (isSuccess) {
                assertThatCode(() -> CrewRequestPolicy.ensureReadRequests(crewMember))
                        .doesNotThrowAnyException();
            } else {
                assertThatThrownBy(() -> CrewRequestPolicy.ensureReadRequests(crewMember))
                        .isExactlyInstanceOf(CrewRequestBusinessException.InsufficientAuthority.class)
                        .hasMessage(CrewRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY.getDescription());
            }
        }

        private static Stream<Arguments> ensureReadRequestsMethodSource() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);

            return Stream.of(
                    Arguments.of(crew.getCrewMembers().get(0), true),
                    Arguments.of(createManagerCrewMember(crew, andong), true),
                    Arguments.of(createGeneralCrewMember(crew, andong), false)
            );
        }
    }

    @Nested
    class ensureAcceptable {

        @ParameterizedTest
        @MethodSource("ensureAcceptableMethodSource")
        @DisplayName("참여 신청 수락은 owner 와 manager 만 가능하다.")
        void test(CrewMember crewMember, boolean isSuccess) {
            if (isSuccess) {
                assertThatCode(() -> CrewRequestPolicy.ensureAcceptable(crewMember))
                        .doesNotThrowAnyException();
            } else {
                assertThatThrownBy(() -> CrewRequestPolicy.ensureAcceptable(crewMember))
                        .isExactlyInstanceOf(CrewRequestBusinessException.InsufficientAuthority.class)
                        .hasMessage(CrewRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY.getDescription());
            }
        }

        private static Stream<Arguments> ensureAcceptableMethodSource() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);

            return Stream.of(
                    Arguments.of(crew.getCrewMembers().get(0), true),
                    Arguments.of(createManagerCrewMember(crew, andong), true),
                    Arguments.of(createGeneralCrewMember(crew, andong), false)
            );
        }
    }

    @Nested
    class ensureRejectable {

        @ParameterizedTest
        @MethodSource("ensureRejectableMethodSource")
        @DisplayName("참여 신청 거절은 owner 와 manager 만 가능하다.")
        void test(CrewMember crewMember, boolean isSuccess) {
            if (isSuccess) {
                assertThatCode(() -> CrewRequestPolicy.ensureRejectable(crewMember))
                        .doesNotThrowAnyException();
            } else {
                assertThatThrownBy(() -> CrewRequestPolicy.ensureRejectable(crewMember))
                        .isExactlyInstanceOf(CrewRequestBusinessException.InsufficientAuthority.class)
                        .hasMessage(CrewRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY.getDescription());
            }
        }

        private static Stream<Arguments> ensureRejectableMethodSource() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);

            return Stream.of(
                    Arguments.of(crew.getCrewMembers().get(0), true),
                    Arguments.of(createManagerCrewMember(crew, andong), true),
                    Arguments.of(createGeneralCrewMember(crew, andong), false)
            );
        }
    }

    private static CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private static CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    private static CrewRequest createCrewRequest(Crew crew, Member revi) {
        return CrewRequest.of(crew, revi, LocalDateTime.now());
    }
}
