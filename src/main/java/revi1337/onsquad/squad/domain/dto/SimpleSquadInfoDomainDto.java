package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Title;

public record SimpleSquadInfoDomainDto(
        Long crewId,
        Long id,
        Title title,
        Capacity capacity,
        Address address,
        String kakaoLink,
        String discordLink,
        Boolean isOwner,
        List<CategoryType> categories,
        SimpleMemberInfoDomainDto squadOwner
) {
    /**
     * Base Constructor For QueryDSL. Used for SquadMemberQueryDslRepository
     */
    @QueryProjection
    public SimpleSquadInfoDomainDto(Long crewId, Long id, Title title, Capacity capacity, Address address,
                                    String kakaoLink, String discordLink, Boolean isOwner,
                                    List<CategoryType> categories, SimpleMemberInfoDomainDto squadOwner) {
        this.crewId = crewId;
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.address = address;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
        this.isOwner = isOwner;
        this.categories = categories;
        this.squadOwner = squadOwner;
    }

    /**
     * For SquadParticipantQueryDslRepository
     */
    @QueryProjection
    public SimpleSquadInfoDomainDto(Long crewId, Long id, Title title, Capacity capacity, Address address,
                                    String kakaoLink, String discordLink, List<CategoryType> categories,
                                    SimpleMemberInfoDomainDto squadOwner) {
        this(crewId, id, title, capacity, address, kakaoLink, discordLink, null, categories, squadOwner);
    }
}
