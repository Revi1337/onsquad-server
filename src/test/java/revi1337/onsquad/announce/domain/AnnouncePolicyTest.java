package revi1337.onsquad.announce.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.error.AnnounceBusinessException;
import revi1337.onsquad.announce.domain.error.AnnounceErrorCode;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;

class AnnouncePolicyTest {

    @Nested
    class CanWrite {

        @ParameterizedTest
        @MethodSource("canWriteMethodSource")
        @DisplayName("manager 이상만 공지사항을 작성할 수 있다.")
        void test(CrewMember crewMember, boolean expected) {
            boolean actual = AnnouncePolicy.canWrite(crewMember);

            assertThat(actual).isEqualTo(expected);
        }

        private static Stream<Arguments> canWriteMethodSource() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(revi);

            return Stream.of(
                    Arguments.of(crew.getCrewMembers().get(0), true),
                    Arguments.of(createManagerCrewMember(crew, andong), true),
                    Arguments.of(createGeneralCrewMember(crew, andong), false)
            );
        }
    }

    @Nested
    class CanPin {

        @ParameterizedTest
        @MethodSource("canPinMethodSource")
        @DisplayName("owner 만 공지사항을 상단에 고정할 수 있다.")
        void test(CrewMember crewMember, boolean expected) {
            boolean actual = AnnouncePolicy.canPin(crewMember);

            assertThat(actual).isEqualTo(expected);
        }

        private static Stream<Arguments> canPinMethodSource() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(revi);

            return Stream.of(
                    Arguments.of(crew.getCrewMembers().get(0), true),
                    Arguments.of(createManagerCrewMember(crew, andong), false),
                    Arguments.of(createGeneralCrewMember(crew, andong), false)
            );
        }
    }

    @Nested
    class canModify {

        @Test
        @DisplayName("본인이 작성한 공지사항을 수정할 수 있다.")
        void canModify1() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(revi);
            CrewMember owner = crew.getCrewMembers().get(0);
            CrewMember manager = createManagerCrewMember(crew, andong);
            Announce announce1 = createCrewAnnounce(crew, revi);
            Announce announce2 = createCrewAnnounce(crew, andong);

            boolean canModify1 = AnnouncePolicy.canModify(owner, owner.getMember().getId());
            boolean canModify2 = AnnouncePolicy.canModify(manager, manager.getMember().getId());

            assertSoftly(softly -> {
                softly.assertThat(canModify1).isTrue();
                softly.assertThat(canModify2).isTrue();
            });
        }

        @Test
        @DisplayName("general 은 공지사항을 수정할 수 없다.")
        void canModify2() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(revi);
            Announce announce = createCrewAnnounce(crew, revi);
            CrewMember general = createGeneralCrewMember(crew, andong);

            boolean canModify1 = AnnouncePolicy.canModify(general, revi.getId());

            assertThat(canModify1).isFalse();
        }

        @Test
        @DisplayName("탈퇴한 사용자가 작성한 공지사항은 manager 이상이면 수정할 수 있다.")
        void canModify3() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(revi);
            CrewMember owner = crew.getCrewMembers().get(0);
            CrewMember manager = createManagerCrewMember(crew, andong);
            Announce announce1 = createCrewAnnounce(crew, revi);
            Announce announce2 = createCrewAnnounce(crew, andong);

            boolean canModify1 = AnnouncePolicy.canModify(owner, null);
            boolean canModify2 = AnnouncePolicy.canModify(manager, null);

            assertSoftly(softly -> {
                softly.assertThat(canModify1).isTrue();
                softly.assertThat(canModify2).isTrue();
            });
        }

        @Test
        @DisplayName("manager 는 본인이 작성한 공지사항만 수정할 수 있다.")
        void canModify4() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(revi);
            CrewMember owner = crew.getCrewMembers().get(0);
            CrewMember manager = createManagerCrewMember(crew, andong);
            Announce announce1 = createCrewAnnounce(crew, revi);
            Announce announce2 = createCrewAnnounce(crew, andong);

            boolean canModify1 = AnnouncePolicy.canModify(manager, revi.getId());
            boolean canModify2 = AnnouncePolicy.canModify(manager, andong.getId());

            assertSoftly(softly -> {
                softly.assertThat(canModify1).isFalse();
                softly.assertThat(canModify2).isTrue();
            });
        }

        @Test
        @DisplayName("owner 는 모든 공지사항을 수정할 수 있다.")
        void canModify5() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(revi);
            CrewMember owner = crew.getCrewMembers().get(0);
            CrewMember manager = createManagerCrewMember(crew, andong);
            Announce announce1 = createCrewAnnounce(crew, revi);
            Announce announce2 = createCrewAnnounce(crew, andong);

            boolean canModify1 = AnnouncePolicy.canModify(owner, revi.getId());
            boolean canModify2 = AnnouncePolicy.canModify(owner, andong.getId());

            assertSoftly(softly -> {
                softly.assertThat(canModify1).isTrue();
                softly.assertThat(canModify2).isTrue();
            });
        }
    }

    @Nested
    class ensureMatchCrew {

        @ParameterizedTest
        @MethodSource("ensureMatchCrewMethodSource")
        @DisplayName("공지사항의 소속 크루 ID 일치 여부를 검증한다")
        void test(Announce announce, Long crewId, boolean isMatch) {
            if (isMatch) {
                assertThatCode(() -> AnnouncePolicy.ensureMatchCrew(announce, crewId))
                        .doesNotThrowAnyException();
            } else {
                assertThatThrownBy(() -> AnnouncePolicy.ensureMatchCrew(announce, crewId))
                        .isExactlyInstanceOf(AnnounceBusinessException.MismatchReference.class)
                        .hasMessage(AnnounceErrorCode.MISMATCH_CREW_REFERENCE.getDescription());
            }
        }

        private static Stream<Arguments> ensureMatchCrewMethodSource() {
            Member revi = createRevi(1L);
            Crew crew = createCrew(1L, revi);
            Announce announce = createCrewAnnounce(crew, revi);

            return Stream.of(
                    Arguments.of(announce, crew.getId(), true),
                    Arguments.of(announce, 999L, false)
            );
        }
    }

    @Nested
    class ensureWritable {

        @ParameterizedTest
        @MethodSource("ensureWritableMethodSource")
        @DisplayName("권한에 따른 공지사항 작성 가능 여부를 검증한다")
        void test(CrewMember crewMember, boolean isSuccess) {
            if (isSuccess) {
                assertThatCode(() -> AnnouncePolicy.ensureWritable(crewMember))
                        .doesNotThrowAnyException();
            } else {
                assertThatThrownBy(() -> AnnouncePolicy.ensureWritable(crewMember))
                        .isExactlyInstanceOf(AnnounceBusinessException.InsufficientAuthority.class)
                        .hasMessage(AnnounceErrorCode.INSUFFICIENT_CREATE_AUTHORITY.getDescription());
            }
        }

        private static Stream<Arguments> ensureWritableMethodSource() {
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
    class ensureModifiable {

        @Test
        @DisplayName("owner 는 모든 공지사항을 수정할 수 있다.")
        void success1() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);
            CrewMember owner = crew.getCrewMembers().get(0);
            CrewMember manager = createManagerCrewMember(crew, andong);
            Announce announce = createCrewAnnounce(crew, andong);

            assertThatCode(() -> AnnouncePolicy.ensureModifiable(announce, owner))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("manager 는 자신이 작성한 공지사항을 수정할 수 있다.")
        void success2() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);
            CrewMember manager = createManagerCrewMember(crew, andong);
            Announce announce = createCrewAnnounce(crew, andong);

            assertThatCode(() -> AnnouncePolicy.ensureModifiable(announce, manager))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("공지사항이 속한 크루가 일치하지 않으면 공지사항 수정에 실패한다.")
        void fail1() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew1 = createCrew(1L, revi);
            CrewMember owner1 = crew1.getCrewMembers().get(0);
            Announce announce = createCrewAnnounce(crew1, revi);
            Crew crew2 = createCrew(2L, andong);
            CrewMember owner2 = crew2.getCrewMembers().get(0);

            assertThatThrownBy(() -> AnnouncePolicy.ensureModifiable(announce, owner2))
                    .isExactlyInstanceOf(AnnounceBusinessException.MismatchReference.class)
                    .hasMessage(AnnounceErrorCode.MISMATCH_CREW_REFERENCE.getDescription());
        }

        @Test
        @DisplayName("manager 보다 권한이 낮으면 공지사항 수정에 실패한다.")
        void fail2() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);
            CrewMember general = createGeneralCrewMember(crew, andong);
            Announce announce = createCrewAnnounce(crew, revi);

            assertThatThrownBy(() -> AnnouncePolicy.ensureModifiable(announce, general))
                    .isExactlyInstanceOf(AnnounceBusinessException.InsufficientAuthority.class)
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY.getDescription());
        }

        @Test
        @DisplayName("manager 는 owner 가 작성한 공지사항을 수정할 수 없다.")
        void fail3() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);
            Announce announce = createCrewAnnounce(crew, revi);
            CrewMember manager = createManagerCrewMember(crew, andong);

            assertThatThrownBy(() -> AnnouncePolicy.ensureModifiable(announce, manager))
                    .isExactlyInstanceOf(AnnounceBusinessException.InsufficientAuthority.class)
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY.getDescription());
        }
    }

    @Nested
    class ensureDeletable {

        @Test
        @DisplayName("owner 는 모든 공지사항을 삭제할 수 있다.")
        void success1() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);
            CrewMember owner = crew.getCrewMembers().get(0);
            CrewMember manager = createManagerCrewMember(crew, andong);
            Announce announce = createCrewAnnounce(crew, andong);

            assertThatCode(() -> AnnouncePolicy.ensureDeletable(announce, owner))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("manager 는 자신이 작성한 공지사항을 삭제할 수 있다.")
        void success2() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);
            CrewMember manager = createManagerCrewMember(crew, andong);
            Announce announce = createCrewAnnounce(crew, andong);

            assertThatCode(() -> AnnouncePolicy.ensureDeletable(announce, manager))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("공지사항이 속한 크루가 일치하지 않으면 공지사항 삭제에 실패한다.")
        void fail1() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew1 = createCrew(1L, revi);
            CrewMember owner1 = crew1.getCrewMembers().get(0);
            Announce announce = createCrewAnnounce(crew1, revi);
            Crew crew2 = createCrew(2L, andong);
            CrewMember owner2 = crew2.getCrewMembers().get(0);

            assertThatThrownBy(() -> AnnouncePolicy.ensureDeletable(announce, owner2))
                    .isExactlyInstanceOf(AnnounceBusinessException.MismatchReference.class)
                    .hasMessage(AnnounceErrorCode.MISMATCH_CREW_REFERENCE.getDescription());
        }

        @Test
        @DisplayName("manager 보다 권한이 낮으면 공지사항 삭제에 실패한다.")
        void fail2() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);
            CrewMember general = createGeneralCrewMember(crew, andong);
            Announce announce = createCrewAnnounce(crew, revi);

            assertThatThrownBy(() -> AnnouncePolicy.ensureDeletable(announce, general))
                    .isExactlyInstanceOf(AnnounceBusinessException.InsufficientAuthority.class)
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY.getDescription());
        }

        @Test
        @DisplayName("manager 는 owner 가 작성한 공지사항을 삭제할 수 없다.")
        void fail3() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);
            Announce announce = createCrewAnnounce(crew, revi);
            CrewMember manager = createManagerCrewMember(crew, andong);

            assertThatThrownBy(() -> AnnouncePolicy.ensureDeletable(announce, manager))
                    .isExactlyInstanceOf(AnnounceBusinessException.InsufficientAuthority.class)
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY.getDescription());
        }
    }

    @Nested
    class ensurePinnable {

        @ParameterizedTest
        @MethodSource("ensurePinnableMethodSource")
        @DisplayName("공지사항 상단 고정은 owner 만 가능하다.")
        void test(CrewMember crewMember, boolean isSuccess) {
            if (isSuccess) {
                assertThatCode(() -> AnnouncePolicy.ensurePinnable(crewMember))
                        .doesNotThrowAnyException();
            } else {
                assertThatThrownBy(() -> AnnouncePolicy.ensurePinnable(crewMember))
                        .isExactlyInstanceOf(AnnounceBusinessException.InsufficientAuthority.class)
                        .hasMessage(AnnounceErrorCode.INSUFFICIENT_PIN_AUTHORITY.getDescription());
            }
        }

        private static Stream<Arguments> ensurePinnableMethodSource() {
            Member revi = createRevi(1L);
            Member andong = createAndong(2L);
            Crew crew = createCrew(1L, revi);

            return Stream.of(
                    Arguments.of(crew.getCrewMembers().get(0), true),
                    Arguments.of(createManagerCrewMember(crew, andong), false),
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

    private static Announce createCrewAnnounce(Crew crew, Member revi) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, revi);
    }
}
