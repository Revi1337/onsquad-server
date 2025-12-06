package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE_1;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE_2;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE_3;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE_4;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE_5;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE_1;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE_2;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE_3;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE_4;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE_5;

import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

public class AnnounceFixture {

    public static Announce ANNOUNCE() {
        return new Announce(ANNOUNCE_TITLE_VALUE, ANNOUNCE_CONTENT_VALUE, null, null);
    }

    public static Announce ANNOUNCE(Crew crew, CrewMember crewMember) {
        return new Announce(ANNOUNCE_TITLE_VALUE, ANNOUNCE_CONTENT_VALUE, crew, crewMember);
    }

    public static Announce FIXED_ANNOUNCE(Crew crew, CrewMember crewMember) {
        Announce announce = new Announce(ANNOUNCE_TITLE_VALUE, ANNOUNCE_CONTENT_VALUE, crew, crewMember);
        announce.fix(LocalDateTime.now());
        return announce;
    }

    public static Announce ANNOUNCE_1(Crew crew, CrewMember crewMember) {
        return new Announce(ANNOUNCE_TITLE_VALUE_1, ANNOUNCE_CONTENT_VALUE_1, crew, crewMember);
    }

    public static Announce ANNOUNCE_2(Crew crew, CrewMember crewMember) {
        return new Announce(ANNOUNCE_TITLE_VALUE_2, ANNOUNCE_CONTENT_VALUE_2, crew, crewMember);
    }

    public static Announce ANNOUNCE_3(Crew crew, CrewMember crewMember) {
        return new Announce(ANNOUNCE_TITLE_VALUE_3, ANNOUNCE_CONTENT_VALUE_3, crew, crewMember);
    }

    public static Announce ANNOUNCE_4(Crew crew, CrewMember crewMember) {
        return new Announce(ANNOUNCE_TITLE_VALUE_4, ANNOUNCE_CONTENT_VALUE_4, crew, crewMember);
    }

    public static Announce ANNOUNCE_5(Crew crew, CrewMember crewMember) {
        return new Announce(ANNOUNCE_TITLE_VALUE_5, ANNOUNCE_CONTENT_VALUE_5, crew, crewMember);
    }
}
