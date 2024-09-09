package revi1337.onsquad.squad_member.presentation.dto.response;

import revi1337.onsquad.squad_category.presentation.dto.SquadCategoryResponse;
import revi1337.onsquad.squad_member.application.dto.SquadWithMemberDto;

import java.util.List;
import java.util.stream.Collectors;

public record SquadWithMemberResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        Boolean isOwner,
        List<SquadCategoryResponse> categories,
        List<SquadMemberResponse> members
) {
    public static SquadWithMemberResponse from(SquadWithMemberDto squadWithMemberDto) {
        return new SquadWithMemberResponse(
                squadWithMemberDto.id(),
                squadWithMemberDto.title(),
                squadWithMemberDto.capacity(),
                squadWithMemberDto.remain(),
                squadWithMemberDto.address(),
                squadWithMemberDto.addressDetail(),
                squadWithMemberDto.kakaoLink(),
                squadWithMemberDto.discordLink(),
                squadWithMemberDto.isOwner(),
                squadWithMemberDto.categories().stream()
                        .map(SquadCategoryResponse::from)
                        .collect(Collectors.toList()),
                squadWithMemberDto.members().stream()
                        .map(SquadMemberResponse::from)
                        .toList()
        );
    }
}
