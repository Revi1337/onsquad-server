package revi1337.onsquad.squad.application.dto.response;

import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad.domain.entity.Squad;
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

    public static SimpleSquadResponse from(Squad squad) {
        return new SimpleSquadResponse(
                squad.getId(),
                squad.getTitle().getValue(),
                squad.getCapacity(),
                squad.getRemain(),
                new ArrayList<>(),
                SimpleMemberDto.from(squad.getMember())
        );
    }

    public static SimpleSquadResponse from(Squad squad, List<CategoryType> categories) {
        return new SimpleSquadResponse(
                squad.getId(),
                squad.getTitle().getValue(),
                squad.getCapacity(),
                squad.getRemain(),
                categories.stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberDto.from(squad.getMember())
        );
    }
}
