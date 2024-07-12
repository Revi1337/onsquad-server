package revi1337.onsquad.squad.dto;

import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Categories;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;

import java.util.List;

public record SquadCreateDto(
        String crewName,
        String title,
        String content,
        int capacity,
        String address,
        String addressDetail,
        List<String> categories,
        String kakaoLink,
        String discordLink
) {
    public Squad toEntity(Member member, Crew crew) {
        return Squad.builder()
                .title(new Title(title))
                .content(new Content(content))
                .capacity(new Capacity(capacity))
                .address(new Address(address, addressDetail))
                .categories(new Categories(categories.toArray(new String[0])))
                .kakaoLink(kakaoLink)
                .discordLink(discordLink)
                .member(member)
                .crew(crew)
                .build();
    }
}
