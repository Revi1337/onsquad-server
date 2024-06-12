package revi1337.onsquad.squad.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import revi1337.onsquad.member.dto.MemberDto;
import revi1337.onsquad.squad.dto.SquadDto;

import java.util.List;

public record SquadCreateRequest(
        @NotEmpty String title,
        @NotEmpty String content,
        @NotNull int capacity,
        @NotEmpty String address,
        String addressDetail,
        @NotEmpty List<String> categories,
        String kakaoLink,
        String discordLink
) {
    public static SquadCreateRequest of(List<String> categories, String city, String cityDetail, int capacity, String title, String content, String kakaoLink, String discordLink) {
        return new SquadCreateRequest(title, content, capacity, city, cityDetail, categories, kakaoLink, discordLink);
    }

    public SquadDto toDto(MemberDto memberDto) {
        return SquadDto.create()
                .categories(categories)
                .address(address)
                .addressDetail(addressDetail)
                .capacity(capacity)
                .title(title)
                .content(content)
                .kakakoLink(kakaoLink)
                .discordLink(discordLink)
                .memberDto(memberDto)
                .build();
    }
}
