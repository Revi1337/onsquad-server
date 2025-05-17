package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.vo.Title;

@Getter
public class SquadInMembersDomainDto {

    private Long id;
    private Title title;
    private int capacity;
    private int remain;
    private Boolean isOwner;
    private SimpleMemberInfoDomainDto owner;
    private List<CategoryType> categories = new ArrayList<>();
    private List<SquadMemberDomainDto> members;

    @QueryProjection
    public SquadInMembersDomainDto(Long id, Title title, int capacity, int remain, Boolean isOwner,
                                   SimpleMemberInfoDomainDto owner, List<SquadMemberDomainDto> members) {
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
