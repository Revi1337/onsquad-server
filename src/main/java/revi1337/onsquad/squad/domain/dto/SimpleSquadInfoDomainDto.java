package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Title;

public record SimpleSquadInfoDomainDto(
        Long crewId,
        Long id,
        Title title,
        Capacity capacity,
        Boolean isLeader,
        List<CategoryType> categories,
        SimpleMemberInfoDomainDto squadOwner
) {
    @QueryProjection
    public SimpleSquadInfoDomainDto(Long crewId, Long id, Title title, Capacity capacity, Boolean isLeader,
                                    List<CategoryType> categories, SimpleMemberInfoDomainDto squadOwner) {
        this.crewId = crewId;
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.isLeader = isLeader;
        this.categories = categories;
        this.squadOwner = squadOwner;
    }
}
