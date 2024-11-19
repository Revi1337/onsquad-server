package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;

public record SquadCreateDto(
        String title,
        String content,
        int capacity,
        String address,
        String addressDetail,
        List<String> categories,
        String kakaoLink,
        String discordLink
) {
    public Squad toEntity(CrewMember crewMember, Crew crew) {
        return Squad.builder()
                .title(new Title(title))
                .content(new Content(content))
                .capacity(new Capacity(capacity))
                .address(new Address(address, addressDetail))
                .kakaoLink(kakaoLink)
                .discordLink(discordLink)
                .crewMember(crewMember)
                .crew(crew)
                .build();
    }
}
