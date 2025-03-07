package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Title;

@Getter
@AllArgsConstructor
public class SquadMembersWithSquadDomainDto {

    private Long id;
    private Title title;
    private Capacity capacity;
    private Boolean isOwner;
    private SimpleMemberInfoDomainDto squadOwner;
    private List<CategoryType> categories;
    private List<SquadMemberDomainDto> members;

    @QueryProjection
    public SquadMembersWithSquadDomainDto(Long id, Title title, Capacity capacity, Boolean isOwner,
                                          SimpleMemberInfoDomainDto squadOwner, List<SquadMemberDomainDto> members) {
        this(id, title, capacity, isOwner, squadOwner, new ArrayList<>(), members);
    }

    public void setCategories(List<CategoryType> categories) {
        this.categories = categories;
    }
}
