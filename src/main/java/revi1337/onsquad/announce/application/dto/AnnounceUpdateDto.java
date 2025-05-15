package revi1337.onsquad.announce.application.dto;

import revi1337.onsquad.announce.domain.Announce;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;

public record AnnounceUpdateDto(
        String title,
        String content
) {
    public Announce toEntity(Crew crew, CrewMember crewMember) {
        return new Announce(title, content, crew, crewMember);
    }
}
