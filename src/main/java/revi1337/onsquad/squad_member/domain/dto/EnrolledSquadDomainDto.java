package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.vo.Title;

public record EnrolledSquadDomainDto(
        Long id,
        Name name,
        String imageUrl,
        SimpleMemberInfoDomainDto owner,
        List<SimpleSquadInfoDomainDto> squads
) {
    @QueryProjection
    public EnrolledSquadDomainDto(Long crewId, Name crewName, String imageUrl, SimpleMemberInfoDomainDto crewOwner) {
        this(crewId, crewName, imageUrl, crewOwner, new ArrayList<>());
    }

    public record SimpleSquadInfoDomainDto(
            Long crewId,
            Long id,
            Title title,
            int capacity,
            int remain,
            Boolean isLeader,
            List<CategoryType> categories,
            SimpleMemberInfoDomainDto squadOwner
    ) {
        @QueryProjection
        public SimpleSquadInfoDomainDto(Long crewId, Long id, Title title, int capacity, int remain, Boolean isLeader,
                                        List<CategoryType> categories, SimpleMemberInfoDomainDto squadOwner) {
            this.crewId = crewId;
            this.id = id;
            this.title = title;
            this.capacity = capacity;
            this.remain = remain;
            this.isLeader = isLeader;
            this.categories = categories;
            this.squadOwner = squadOwner;
        }
    }
}
