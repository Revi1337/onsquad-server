package revi1337.onsquad.squad.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad.domain.result.SquadResult;

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
        SimpleMemberDto leader
) {

    public static SquadResponse from(SquadResult squadResult) {
        return new SquadResponse(
                squadResult.id(),
                squadResult.title().getValue(),
                squadResult.content().getValue(),
                squadResult.capacity(),
                squadResult.remain(),
                squadResult.address().getValue(),
                squadResult.address().getDetail(),
                squadResult.kakaoLink(),
                squadResult.discordLink(),
                squadResult.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberDto.from(squadResult.leader())
        );
    }
}
