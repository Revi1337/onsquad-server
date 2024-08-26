package revi1337.onsquad.squad.dto.response;

import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.dto.SquadDto;

import java.util.List;

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
