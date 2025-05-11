package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_DETAIL_VALUE_1;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_DETAIL_VALUE_2;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_DETAIL_VALUE_3;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_VALUE_1;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_VALUE_2;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_VALUE_3;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CAPACITY_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CAPACITY_VALUE_1;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CAPACITY_VALUE_2;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CAPACITY_VALUE_3;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CONTENT_VALUE_1;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CONTENT_VALUE_2;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CONTENT_VALUE_3;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_DISCORD_LINK_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_DISCORD_LINK_VALUE_1;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_DISCORD_LINK_VALUE_2;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_DISCORD_LINK_VALUE_3;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_KAKAO_LINK_VALUE_1;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_KAKAO_LINK_VALUE_2;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_KAKAO_LINK_VALUE_3;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_TITLE_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_TITLE_VALUE_1;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_TITLE_VALUE_2;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_TITLE_VALUE_3;

import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.Squad.SquadMetadata;

public class SquadFixture {

    public static Squad SQUAD() {
        return Squad.create(new SquadMetadata(
                SQUAD_TITLE_VALUE,
                SQUAD_CONTENT_VALUE,
                SQUAD_CAPACITY_VALUE,
                SQUAD_ADDRESS_VALUE,
                SQUAD_ADDRESS_DETAIL_VALUE,
                SQUAD_KAKAO_LINK_VALUE,
                SQUAD_DISCORD_LINK_VALUE
        ));
    }

    public static Squad SQUAD_1() {
        return Squad.create(new SquadMetadata(
                SQUAD_TITLE_VALUE_1,
                SQUAD_CONTENT_VALUE_1,
                SQUAD_CAPACITY_VALUE_1,
                SQUAD_ADDRESS_VALUE_1,
                SQUAD_ADDRESS_DETAIL_VALUE_1,
                SQUAD_KAKAO_LINK_VALUE_1,
                SQUAD_DISCORD_LINK_VALUE_1
        ));
    }

    public static Squad SQUAD_2() {
        return Squad.create(new SquadMetadata(
                SQUAD_TITLE_VALUE_2,
                SQUAD_CONTENT_VALUE_2,
                SQUAD_CAPACITY_VALUE_2,
                SQUAD_ADDRESS_VALUE_2,
                SQUAD_ADDRESS_DETAIL_VALUE_2,
                SQUAD_KAKAO_LINK_VALUE_2,
                SQUAD_DISCORD_LINK_VALUE_2
        ));
    }

    public static Squad SQUAD_3() {
        return Squad.create(new SquadMetadata(
                SQUAD_TITLE_VALUE_3,
                SQUAD_CONTENT_VALUE_3,
                SQUAD_CAPACITY_VALUE_3,
                SQUAD_ADDRESS_VALUE_3,
                SQUAD_ADDRESS_DETAIL_VALUE_3,
                SQUAD_KAKAO_LINK_VALUE_3,
                SQUAD_DISCORD_LINK_VALUE_3
        ));
    }

    public static Squad SQUAD(CrewMember crewMember, Crew crew, int capacity) {
        SquadMetadata data = new SquadMetadata(
                SQUAD_TITLE_VALUE,
                SQUAD_CONTENT_VALUE,
                capacity,
                SQUAD_ADDRESS_VALUE,
                SQUAD_ADDRESS_DETAIL_VALUE,
                SQUAD_KAKAO_LINK_VALUE,
                SQUAD_DISCORD_LINK_VALUE
        );
        return Squad.create(data, crewMember, crew);
    }

    public static Squad SQUAD(CrewMember crewMember, Crew crew) {
        SquadMetadata data = new SquadMetadata(
                SQUAD_TITLE_VALUE,
                SQUAD_CONTENT_VALUE,
                SQUAD_CAPACITY_VALUE,
                SQUAD_ADDRESS_VALUE,
                SQUAD_ADDRESS_DETAIL_VALUE,
                SQUAD_KAKAO_LINK_VALUE,
                SQUAD_DISCORD_LINK_VALUE
        );
        return Squad.create(data, crewMember, crew);
    }

    public static Squad SQUAD_1(CrewMember crewMember, Crew crew) {
        SquadMetadata data = new SquadMetadata(
                SQUAD_TITLE_VALUE_1,
                SQUAD_CONTENT_VALUE_1,
                SQUAD_CAPACITY_VALUE_1,
                SQUAD_ADDRESS_VALUE_1,
                SQUAD_ADDRESS_DETAIL_VALUE_1,
                SQUAD_KAKAO_LINK_VALUE_1,
                SQUAD_DISCORD_LINK_VALUE_1
        );
        return Squad.create(data, crewMember, crew);
    }

    public static Squad SQUAD_2(CrewMember crewMember, Crew crew) {
        SquadMetadata data = new SquadMetadata(
                SQUAD_TITLE_VALUE_2,
                SQUAD_CONTENT_VALUE_2,
                SQUAD_CAPACITY_VALUE_2,
                SQUAD_ADDRESS_VALUE_2,
                SQUAD_ADDRESS_DETAIL_VALUE_2,
                SQUAD_KAKAO_LINK_VALUE_2,
                SQUAD_DISCORD_LINK_VALUE_2
        );
        return Squad.create(data, crewMember, crew);
    }

    public static Squad SQUAD_3(CrewMember crewMember, Crew crew) {
        SquadMetadata data = new SquadMetadata(
                SQUAD_TITLE_VALUE_3,
                SQUAD_CONTENT_VALUE_3,
                SQUAD_CAPACITY_VALUE_3,
                SQUAD_ADDRESS_VALUE_3,
                SQUAD_ADDRESS_DETAIL_VALUE_3,
                SQUAD_KAKAO_LINK_VALUE_3,
                SQUAD_DISCORD_LINK_VALUE_3
        );
        return Squad.create(data, crewMember, crew);
    }
}
