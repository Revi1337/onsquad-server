package revi1337.onsquad.squad.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.application.dto.SquadDto;

public record SquadResponse(
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
        SimpleMemberInfoResponse memberInfo
) {
    public static SquadResponse from(SquadDto dto) {
        return new SquadResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.capacity(),
                dto.remain(),
                dto.address(),
                dto.addressDetail(),
                dto.kakaoLink(),
                dto.discordLink(),
                dto.categories(),
                SimpleMemberInfoResponse.from(dto.memberInfo())
        );
    }
}
