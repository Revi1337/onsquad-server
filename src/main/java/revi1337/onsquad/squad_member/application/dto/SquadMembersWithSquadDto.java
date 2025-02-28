package revi1337.onsquad.squad_member.application.dto;

import java.util.List;
import java.util.stream.Collectors;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMembersWithSquadDomainDto;

public record SquadMembersWithSquadDto(
        Long id,
        String title,
        int capacity,
        int remain,
        Boolean isOwner,
        SimpleMemberInfoDto owner,
        List<String> categories,
        List<SquadMemberDto> members
) {
    public static SquadMembersWithSquadDto from(SquadMembersWithSquadDomainDto squadMembersWithSquadDomainDto) {
        return new SquadMembersWithSquadDto(
                squadMembersWithSquadDomainDto.getId(),
                squadMembersWithSquadDomainDto.getTitle().getValue(),
                squadMembersWithSquadDomainDto.getCapacity().getValue(),
                squadMembersWithSquadDomainDto.getCapacity().getRemain(),
                squadMembersWithSquadDomainDto.getIsOwner(),
                SimpleMemberInfoDto.from(squadMembersWithSquadDomainDto.getSquadOwner()),
                squadMembersWithSquadDomainDto.getCategories().stream()
                        .map(CategoryType::getText)
                        .collect(Collectors.toList()),
                squadMembersWithSquadDomainDto.getMembers().stream()
                        .map(SquadMemberDto::from)
                        .toList()
        );
    }
}
