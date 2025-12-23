package revi1337.onsquad.squad_member.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.SquadLinkable;
import revi1337.onsquad.squad.domain.result.SimpleSquadResult;

public record MyParticipantSquadResult(
        Long crewId,
        Boolean isLeader,
        SimpleSquadResult squad
) implements SquadLinkable {

    @QueryProjection
    public MyParticipantSquadResult(Long crewId, Boolean isLeader, SimpleSquadResult squad) {
        this.crewId = crewId;
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
