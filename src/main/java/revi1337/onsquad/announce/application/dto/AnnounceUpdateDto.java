package revi1337.onsquad.announce.application.dto;

import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

public record AnnounceUpdateDto(
        String title,
        String content
) {

    public Announce toEntity(Crew crew, CrewMember crewMember) {
        return new Announce(title, content, crew, crewMember);
    }
}
