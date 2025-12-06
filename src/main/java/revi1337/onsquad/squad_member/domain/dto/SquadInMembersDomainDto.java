package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;
import revi1337.onsquad.squad.domain.entity.vo.Title;

@Getter
public class SquadInMembersDomainDto {

    private Long id;
    private Title title;
    private int capacity;
    private int remain;
    private Boolean isOwner;
    private SimpleMemberDomainDto owner;
    private List<CategoryType> categories = new ArrayList<>();
    private List<SquadMemberDomainDto> members;

    @QueryProjection
    public SquadInMembersDomainDto(Long id, Title title, int capacity, int remain, Boolean isOwner,
                                   SimpleMemberDomainDto owner, List<SquadMemberDomainDto> members) {
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.remain = remain;
        this.isOwner = isOwner;
        this.owner = owner;
        this.members = members;
    }

    public void registerCategories(List<CategoryType> categories) {
        this.categories = categories;
    }
}
