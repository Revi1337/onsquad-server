package revi1337.onsquad.squad.domain.model;

import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.SquadLinkable;

public record SquadWithLeaderState(
        Boolean isLeader,
        SimpleSquad squad
) implements SquadLinkable {

    @Override
    public Long getSquadId() {
        return squad.id();
    }

    @Override
    public void addCategories(List<CategoryType> categories) {
        squad.addCategories(categories);
    }
}
