package revi1337.onsquad.squad.application.dto.response;

import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad.domain.result.SimpleSquadResult;

public record SimpleSquadResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberDto leader
) {

    public static SimpleSquadResponse from(SimpleSquadResult simpleSquadResult) {
        return new SimpleSquadResponse(
                simpleSquadResult.id(),
                simpleSquadResult.title().getValue(),
                simpleSquadResult.capacity(),
                simpleSquadResult.capacity(),
                simpleSquadResult.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberDto.from(simpleSquadResult.leader())
        );
    }
}
