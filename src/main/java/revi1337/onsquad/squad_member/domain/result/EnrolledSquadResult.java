package revi1337.onsquad.squad_member.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;
import revi1337.onsquad.squad.domain.entity.vo.Title;

public record EnrolledSquadResult(
        Long id,
        Name name,
        String imageUrl,
        SimpleMemberDomainDto owner,
        List<SimpleSquadInfoDomainDto> squads
) {

    @QueryProjection
    public EnrolledSquadResult(Long crewId, Name crewName, String imageUrl, SimpleMemberDomainDto crewOwner) {
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
            SimpleMemberDomainDto squadOwner
    ) {

        @QueryProjection
        public SimpleSquadInfoDomainDto(Long crewId, Long id, Title title, int capacity, int remain, Boolean isLeader,
                                        List<CategoryType> categories, SimpleMemberDomainDto squadOwner) {
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
