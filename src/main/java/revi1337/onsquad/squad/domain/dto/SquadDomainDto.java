package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;
import revi1337.onsquad.member.domain.entity.vo.Address;
import revi1337.onsquad.squad.domain.entity.vo.Content;
import revi1337.onsquad.squad.domain.entity.vo.Title;

public record SquadDomainDto(
        Long id,
        Title title,
        Content content,
        int capacity,
        int remain,
        Address address,
        String kakaoLink,
        String discordLink,
        List<CategoryType> categories,
        SimpleMemberDomainDto leader
) {

    @QueryProjection
    public SquadDomainDto(Long id, Title title, Content content, int capacity, int remain, Address address,
                          String kakaoLink, String discordLink, SimpleMemberDomainDto owner) {
        this(id, title, content, capacity, remain, address, kakaoLink, discordLink, new ArrayList<>(), owner);
    }

    @QueryProjection
    public SquadDomainDto(Long id, Title title, Content content, int capacity, int remain, Address address,
                          String kakaoLink, String discordLink, List<CategoryType> categories,
                          SimpleMemberDomainDto leader) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.remain = remain;
        this.address = address;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
        this.categories = categories;
        this.leader = leader;
    }
}
