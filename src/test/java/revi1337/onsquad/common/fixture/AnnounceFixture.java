package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE;

import revi1337.onsquad.announce.domain.Announce;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;

public class AnnounceFixture {

    public static Announce ANNOUNCE(Crew crew, CrewMember crewMember) {
        return Announce.builder()
                .crew(crew)
                .crewMember(crewMember)
                .title(ANNOUNCE_TITLE)
                .content(ANNOUNCE_CONTENT_VALUE)
                .build();
    }
}
