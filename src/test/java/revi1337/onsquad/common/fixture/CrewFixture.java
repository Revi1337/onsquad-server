package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;

import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

public class CrewFixture {

    public static Crew CREW() {
        return Crew.create(null, CREW_NAME_VALUE, CREW_INTRODUCE_VALUE, CREW_DETAIL_VALUE, null, null);
    }

    public static Crew CREW(Member member) {
        return Crew.create(member, CREW_NAME_VALUE, CREW_INTRODUCE_VALUE, CREW_DETAIL_VALUE, null, null);
    }

    public static Crew CREW_1(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 1, CREW_INTRODUCE_VALUE + 1, CREW_DETAIL_VALUE + 1, null, null);
    }

    public static Crew CREW_2(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 2, CREW_INTRODUCE_VALUE + 2, CREW_DETAIL_VALUE + 2, null, null);
    }

    public static Crew CREW_3(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 3, CREW_INTRODUCE_VALUE + 3, CREW_DETAIL_VALUE + 3, null, null);
    }

    public static Crew CREW_4(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 4, CREW_INTRODUCE_VALUE + 4, CREW_DETAIL_VALUE + 4, null, null);
    }

    public static Crew CREW_5(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 5, CREW_INTRODUCE_VALUE + 5, CREW_DETAIL_VALUE + 5, null, null);
    }

    public static Crew CREW_6(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 6, CREW_INTRODUCE_VALUE + 6, CREW_DETAIL_VALUE + 6, null, null);
    }

    public static Crew CREW_7(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 7, CREW_INTRODUCE_VALUE + 7, CREW_DETAIL_VALUE + 7, null, null);
    }

    public static Crew CREW_8(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 8, CREW_INTRODUCE_VALUE + 8, CREW_DETAIL_VALUE + 8, null, null);
    }

    public static Crew CREW_9(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 9, CREW_INTRODUCE_VALUE + 9, CREW_DETAIL_VALUE + 9, null, null);
    }

    public static Crew CREW_10(Member member) {
        return Crew.create(member, CREW_NAME_VALUE + 10, CREW_INTRODUCE_VALUE + 10, CREW_DETAIL_VALUE + 10, null, null);
    }

    public static Crew CREW_WITH_IMAGE(Member member, String imageUrl) {
        return Crew.create(member, CREW_NAME_VALUE, CREW_INTRODUCE_VALUE, CREW_DETAIL_VALUE, null, imageUrl);
    }

    public static Crew CREW_WITH_NAME_AND_IMAGE(Member member, String name, String imageUrl) {
        return Crew.create(member, name, CREW_INTRODUCE_VALUE, CREW_DETAIL_VALUE, null, imageUrl);
    }
}
