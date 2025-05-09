package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Title;

public record SquadDomainDto(
        Long id,
        Title title,
        Capacity capacity,
        List<CategoryType> categories,
        SimpleMemberInfoDomainDto owner
) {
    @QueryProjection
    public SquadDomainDto(Long id, Title title, Capacity capacity, SimpleMemberInfoDomainDto owner) {
        this(id, title, capacity, new ArrayList<>(), owner);
    }

    public SquadDomainDto(Long id, Title title, Capacity capacity, List<CategoryType> categories,
                          SimpleMemberInfoDomainDto owner) {
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.categories = categories;
        this.owner = owner;
    }

    public void addCategories(List<CategoryType> categories) {
        this.categories.addAll(categories);
    }
}
