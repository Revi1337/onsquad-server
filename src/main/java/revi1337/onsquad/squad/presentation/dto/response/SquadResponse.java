package revi1337.onsquad.squad.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.application.dto.SquadDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
        SimpleMemberInfoResponse leader
) {
    public static SquadResponse from(SquadDto squadDto) {
        return new SquadResponse(
                squadDto.id(),
                squadDto.title(),
                squadDto.content(),
                squadDto.capacity(),
                squadDto.remain(),
                squadDto.address(),
                squadDto.addressDetail(),
                squadDto.kakaoLink(),
                squadDto.discordLink(),
                squadDto.categories(),
                SimpleMemberInfoResponse.from(squadDto.leader())
        );
    }
}
