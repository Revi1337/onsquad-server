package revi1337.onsquad.crew_member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.domain.error.CrewMemberErrorCode;
import revi1337.onsquad.member.domain.entity.Member;

class CrewMemberPolicyTest {

    @Test
    void isOwner() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = crew.getCrewMembers().get(0);

        boolean isOwner = CrewMemberPolicy.isOwner(me);

        assertThat(isOwner).isTrue();
    }

    @Test
    void isManager() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createManagerCrewMember(crew, createAndong(2L));

        boolean manager = CrewMemberPolicy.isManager(me);

        assertThat(manager).isTrue();
    }

    @Test
    void isGeneral() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createGeneralCrewMember(crew, createAndong(2L));

        boolean general = CrewMemberPolicy.isGeneral(me);

        assertThat(general).isTrue();
    }

    @Test
    void isManagerOrHigher1() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createManagerCrewMember(crew, createAndong(2L));

        boolean isManagerOrHigher = CrewMemberPolicy.isManagerOrHigher(me);

        assertThat(isManagerOrHigher).isTrue();
    }

    @Test
    void isManagerOrHigher2() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createGeneralCrewMember(crew, createAndong(2L));

        boolean isManagerOrHigher = CrewMemberPolicy.isManagerOrHigher(me);

        assertThat(isManagerOrHigher).isFalse();
    }

    @Test
    void isManagerOrHigher3() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = crew.getCrewMembers().get(0);

        boolean isManagerOrHigher = CrewMemberPolicy.isManagerOrHigher(me);

        assertThat(isManagerOrHigher).isTrue();
    }

    @Test
    void isLowerThanManager1() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createManagerCrewMember(crew, createAndong(2L));

        boolean isLowerThanManager = CrewMemberPolicy.isLowerThanManager(me);

        assertThat(isLowerThanManager).isFalse();
    }

    @Test
    void isLowerThanManager2() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createGeneralCrewMember(crew, createAndong(2L));

        boolean isLowerThanManager = CrewMemberPolicy.isLowerThanManager(me);

        assertThat(isLowerThanManager).isTrue();
    }

    @Test
    void isLowerThanManager3() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = crew.getCrewMembers().get(0);

        boolean isLowerThanManager = CrewMemberPolicy.isLowerThanManager(me);

        assertThat(isLowerThanManager).isFalse();
    }

    @Test
    void isManagerOrLower1() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createManagerCrewMember(crew, createAndong(2L));

        boolean isManagerOrLower = CrewMemberPolicy.isManagerOrLower(me);

        assertThat(isManagerOrLower).isTrue();
    }

    @Test
    void isManagerOrLower2() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createGeneralCrewMember(crew, createAndong(2L));

        boolean isLowerThanManager = CrewMemberPolicy.isManagerOrLower(me);

        assertThat(isLowerThanManager).isTrue();
    }

    @Test
    void isManagerOrLower3() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = crew.getCrewMembers().get(0);

        boolean isLowerThanManager = CrewMemberPolicy.isManagerOrLower(me);

        assertThat(isLowerThanManager).isFalse();
    }

    @Test
    void isNotOwner1() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createGeneralCrewMember(crew, createAndong(2L));

        boolean isNotOwner = CrewMemberPolicy.isNotOwner(me);

        assertThat(isNotOwner).isTrue();
    }

    @Test
    void isNotOwner2() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = crew.getCrewMembers().get(0);

        boolean isNotOwner = CrewMemberPolicy.isNotOwner(me);

        assertThat(isNotOwner).isFalse();
    }

    @Test
    void isMe1() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createManagerCrewMember(crew, createAndong(2L));

        boolean isMe = CrewMemberPolicy.isMe(me, me);

        assertThat(isMe).isTrue();
    }

    @Test
    void isMe2() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember me = createManagerCrewMember(crew, createAndong(2L));

        boolean isMe = CrewMemberPolicy.isMe(me, me);

        assertThat(isMe).isTrue();
    }

    @Test
    @DisplayName("자기 자신은 추방 시킬 수 없다")
    void canKick1() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember owner = crew.getCrewMembers().get(0);

        boolean canKickOther = CrewMemberPolicy.canKick(owner, owner);

        assertThat(canKickOther).isFalse();
    }

    @Test
    @DisplayName("자신이 manager 이면, general 유저만 추방시킬 수 있다")
    void canKick2() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember owner = crew.getCrewMembers().get(0);
        CrewMember manger = createManagerCrewMember(crew, createAndong(2L));
        CrewMember general = createGeneralCrewMember(crew, createKwangwon(3L));
        crew.addCrewMember(manger, general);

        boolean canKickOther1 = CrewMemberPolicy.canKick(manger, owner);
        boolean canKickOther2 = CrewMemberPolicy.canKick(manger, manger);
        boolean canKickOther3 = CrewMemberPolicy.canKick(manger, general);

        assertSoftly(softly -> {
            softly.assertThat(canKickOther1).isFalse();
            softly.assertThat(canKickOther2).isFalse();
            softly.assertThat(canKickOther3).isTrue();
        });
    }

    @Test
    @DisplayName("자신이 owner 이면, 모든 유저를 추방시킬 수 있다")
    void canKick3() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember owner = crew.getCrewMembers().get(0);
        CrewMember manger = createManagerCrewMember(crew, createAndong(2L));
        CrewMember general = createGeneralCrewMember(crew, createKwangwon(3L));
        crew.addCrewMember(manger, general);

        boolean canKickOther1 = CrewMemberPolicy.canKick(owner, manger);
        boolean canKickOther2 = CrewMemberPolicy.canKick(owner, general);

        assertSoftly(softly -> {
            softly.assertThat(canKickOther1).isTrue();
            softly.assertThat(canKickOther2).isTrue();
        });
    }

    @Test
    @DisplayName("자기 자신에게 owner 를 위임할 수 없다")
    void canDelegateOwner1() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember owner = crew.getCrewMembers().get(0);

        boolean canDelegateOwner = CrewMemberPolicy.canDelegateOwner(owner, owner);

        assertThat(canDelegateOwner).isFalse();
    }

    @Test
    @DisplayName("자신이 owner 가 아니면 owner 를 위임할 수 없다")
    void canDelegateOwner2() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember owner = crew.getCrewMembers().get(0);
        CrewMember manger = createManagerCrewMember(crew, createAndong(2L));
        CrewMember general = createGeneralCrewMember(crew, createKwangwon(3L));
        crew.addCrewMember(manger, general);

        boolean canDelegateOwner = CrewMemberPolicy.canDelegateOwner(general, manger);

        assertThat(canDelegateOwner).isFalse();
    }

    @Test
    @DisplayName("소속 크루가 서로 다른 멤버 간에는 추방하려하면 예외가 발생한다")
    void ensureKickable() {
        Crew crew1 = createCrew(1L, createRevi(1L));
        CrewMember owner = crew1.getCrewMembers().get(0);
        Crew crew2 = createCrew(2L, createAndong(2L));
        CrewMember otherCrewGeneral = createGeneralCrewMember(crew2, createKwangwon(3L));
        crew2.addCrewMember(otherCrewGeneral);

        assertThatThrownBy(() -> CrewMemberPolicy.ensureKickable(owner, otherCrewGeneral))
                .isExactlyInstanceOf(CrewMemberBusinessException.MismatchReference.class)
                .hasMessage(CrewMemberErrorCode.MISMATCH_CREW_REFERENCE.getDescription());
    }

    @Test
    @DisplayName("owner 가 아닌 사용자가 owner 를 위임하려하면 예외가 발생한다")
    void ensureCanDelegateOwner() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember owner = crew.getCrewMembers().get(0);
        CrewMember manger = createManagerCrewMember(crew, createAndong(2L));
        CrewMember general = createGeneralCrewMember(crew, createKwangwon(3L));
        crew.addCrewMember(manger, general);

        assertThatThrownBy(() -> CrewMemberPolicy.ensureOwnerDelegatable(manger))
                .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class)
                .hasMessage(CrewMemberErrorCode.INSUFFICIENT_DELEGATE_OWNER_AUTHORITY.getDescription());
        assertThatThrownBy(() -> CrewMemberPolicy.ensureOwnerDelegatable(general))
                .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class)
                .hasMessage(CrewMemberErrorCode.INSUFFICIENT_DELEGATE_OWNER_AUTHORITY.getDescription());
    }

    @Test
    @DisplayName("자기 자신에게 어떤 행동을 취하려하면 예외가 발생한다")
    void ensureNotSelfTarget() {
        Crew crew = createCrew(1L, createRevi(1L));
        CrewMember owner = crew.getCrewMembers().get(0);

        assertThatThrownBy(() -> CrewMemberPolicy.ensureNotSelfTargeting(owner.getMember().getId(), owner.getMember().getId()))
                .isExactlyInstanceOf(CrewMemberBusinessException.InvalidRequest.class)
                .hasMessage(CrewMemberErrorCode.CANNOT_TARGET_SELF.getDescription());
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
