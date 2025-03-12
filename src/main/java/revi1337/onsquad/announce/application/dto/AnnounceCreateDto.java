package revi1337.onsquad.announce.application.dto;

import revi1337.onsquad.announce.domain.Announce;
import revi1337.onsquad.announce.domain.vo.Title;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;

public record AnnounceCreateDto(
        String title,
        String content
) {
    public Announce toEntity(Crew crew, CrewMember crewMember) {
        return Announce.builder()
                .title(new Title(title))
                .content(content)
                .crew(crew)
                .crewMember(crewMember)
                .build();
    }
}
