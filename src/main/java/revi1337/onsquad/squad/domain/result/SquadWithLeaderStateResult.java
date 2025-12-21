package revi1337.onsquad.squad.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.SquadLinkable;

public record SquadWithLeaderStateResult(
        Boolean isLeader,
        SimpleSquadResult squad
) implements SquadLinkable {

    @QueryProjection
    public SquadWithLeaderStateResult(Boolean isLeader, SimpleSquadResult squad) {
        this.isLeader = isLeader;
        this.squad = squad;
    }

    @Override
    public Long getSquadId() {
        return squad.id();
    }

    @Override
    public void addCategories(List<CategoryType> categories) {
        squad.addCategories(categories);
    }
}
