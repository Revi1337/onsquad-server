package revi1337.onsquad.squad_member.application.dto;

import revi1337.onsquad.squad_category.application.dto.SquadCategoryDto;
import revi1337.onsquad.squad_member.domain.dto.SquadWithMemberDomainDto;

import java.util.List;
import java.util.stream.Collectors;

public record SquadWithMemberDto(
        Long id,
        String title,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        Boolean isOwner,
        List<SquadCategoryDto> categories,
        List<SquadMemberDto> members
) {
    public static SquadWithMemberDto from(SquadWithMemberDomainDto squadWithMemberDomainDto) {
        return new SquadWithMemberDto(
                squadWithMemberDomainDto.id(),
                squadWithMemberDomainDto.title().getValue(),
                squadWithMemberDomainDto.capacity().getValue(),
                squadWithMemberDomainDto.capacity().getRemain(),
                squadWithMemberDomainDto.address().getValue(),
                squadWithMemberDomainDto.address().getDetail(),
                squadWithMemberDomainDto.kakaoLink(),
                squadWithMemberDomainDto.discordLink(),
                squadWithMemberDomainDto.isOwner(),
                squadWithMemberDomainDto.categories().stream()
                        .map(SquadCategoryDto::from)
                        .collect(Collectors.toList()),
                squadWithMemberDomainDto.members().stream()
                        .map(SquadMemberDto::from)
                        .toList()
        );
    }
}
