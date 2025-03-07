package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoWithOwnerFlagDomainDto;

public record SimpleSquadInfoWithOwnerFlagDto(
        Long id,
        String title,
        int capacity,
        int remain,
        Boolean isOwner,
        List<String> categories,
        SimpleMemberInfoDto squadOwner
) {
    public static SimpleSquadInfoWithOwnerFlagDto from(SimpleSquadInfoWithOwnerFlagDomainDto domainDto) {
        return new SimpleSquadInfoWithOwnerFlagDto(
                domainDto.getId(),
                domainDto.getTitle().getValue(),
                domainDto.getCapacity().getValue(),
                domainDto.getCapacity().getRemain(),
                domainDto.getIsOwner(),
                domainDto.getCategories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberInfoDto.from(domainDto.getSquadOwner())
        );
    }
}
