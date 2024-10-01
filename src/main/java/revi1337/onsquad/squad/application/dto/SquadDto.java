package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.squad_category.domain.SquadCategory;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.Squad;

import java.util.List;
import java.util.stream.Collectors;

public record SquadDto(
        Long id,
        String title,
        String content,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        List<String> categories,
        SimpleMemberInfoDto memberInfo
) {
    public static SquadDto from(Squad squad) {
        return new SquadDto(
                squad.getId(),
                squad.getTitle().getValue(),
                squad.getContent().getValue(),
                squad.getCapacity().getValue(),
                squad.getCapacity().getRemain(),
                squad.getAddress().getValue(),
                squad.getAddress().getDetail(),
                squad.getKakaoLink(),
                squad.getDiscordLink(),
                squad.getCategories().stream()
                        .map(SquadCategory::getCategory)
                        .map(category -> category.getCategoryType().getText())
                        .collect(Collectors.toList()),
                SimpleMemberInfoDto.from(squad.getCrewMember().getMember())
        );
    }
}
