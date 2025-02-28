package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Title;

@Getter
public class SimpleSquadInfoWithOwnerFlagDomainDto {

    private Long id;
    private Title title;
    private Capacity capacity;
    private Boolean isOwner;
    private List<CategoryType> categories = new ArrayList<>();
    private SimpleMemberInfoDomainDto squadOwner;

    @QueryProjection
    public SimpleSquadInfoWithOwnerFlagDomainDto(Long id, Title title, Capacity capacity,
                                                 Boolean isOwner, SimpleMemberInfoDomainDto squadOwner) {
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.isOwner = isOwner;
        this.squadOwner = squadOwner;
    }

    public void setCategories(List<CategoryType> categories) {
        this.categories = categories;
    }
}
