package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;

public record SquadInfoDto(
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
        SimpleMemberInfoDto squadOwner
) {
    public static SquadInfoDto from(SquadInfoDomainDto squadInfoDomainDto) {
        return new SquadInfoDto(
                squadInfoDomainDto.id(),
                squadInfoDomainDto.title().getValue(),
                squadInfoDomainDto.content().getValue(),
                squadInfoDomainDto.capacity().getValue(),
                squadInfoDomainDto.capacity().getRemain(),
                squadInfoDomainDto.address().getValue(),
                squadInfoDomainDto.address().getDetail(),
                squadInfoDomainDto.kakaoLink(),
                squadInfoDomainDto.discordLink(),
                squadInfoDomainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberInfoDto.from(squadInfoDomainDto.squadOwner())
        );
    }
}
