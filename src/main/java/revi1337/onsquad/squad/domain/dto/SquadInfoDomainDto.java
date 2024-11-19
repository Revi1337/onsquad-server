package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;

public record SquadInfoDomainDto(
        Long id,
        Title title,
        Content content,
        Capacity capacity,
        Address address,
        String kakaoLink,
        String discordLink,
        List<CategoryType> categories,
        SimpleMemberInfoDomainDto squadOwner
) {
    @QueryProjection
    public SquadInfoDomainDto(Long id, Title title, Content content, Capacity capacity, Address address,
                              String kakaoLink, String discordLink, List<CategoryType> categories,
                              SimpleMemberInfoDomainDto squadOwner) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.address = address;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
        this.categories = categories;
        this.squadOwner = squadOwner;
    }
}
