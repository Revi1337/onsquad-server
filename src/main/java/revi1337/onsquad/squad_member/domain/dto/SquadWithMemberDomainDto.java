package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Title;

import java.util.List;
import java.util.Set;

public record SquadWithMemberDomainDto(
        Long id,
        Title title,
        Capacity capacity,
        Address address,
        String kakaoLink,
        String discordLink,
        Boolean isOwner,
        Set<CategoryType> categories,
        List<SquadMemberDomainDto> members
) {
    @QueryProjection
    public SquadWithMemberDomainDto(Long id, Title title, Capacity capacity, Address address, String kakaoLink, String discordLink, Boolean isOwner, Set<CategoryType> categories, List<SquadMemberDomainDto> members) {
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.address = address;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
        this.isOwner = isOwner;
        this.categories = categories;
        this.members = members;
    }
}
