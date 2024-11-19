package revi1337.onsquad.squad_member.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.squad_member.application.dto.SquadWithMemberDto;

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
        List<String> categories,
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
                squadWithMemberDto.categories(),
                squadWithMemberDto.members().stream()
                        .map(SquadMemberResponse::from)
                        .toList()
        );
    }
}
