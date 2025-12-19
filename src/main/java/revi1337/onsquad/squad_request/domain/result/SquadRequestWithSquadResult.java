package revi1337.onsquad.squad_request.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;
import revi1337.onsquad.squad.domain.entity.vo.Title;

public record SquadRequestWithSquadResult(
        Long crewId,
        Long id,
        Title title,
        int capacity,
        int remain,
        List<CategoryType> categories,
        SimpleMemberDomainDto squadOwner,
        RequestParticipantDomainDto request
) {

    @QueryProjection
    public SquadRequestWithSquadResult(Long crewId, Long id, Title title, int capacity, int remain,
                                       List<CategoryType> categories, SimpleMemberDomainDto squadOwner,
                                       RequestParticipantDomainDto request) {
        this.crewId = crewId;
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.remain = remain;
        this.categories = categories;
        this.squadOwner = squadOwner;
        this.request = request;
    }

    public record RequestParticipantDomainDto(
            Long id,
            LocalDateTime requestAt
    ) {

        @QueryProjection
        public RequestParticipantDomainDto(Long id, LocalDateTime requestAt) {
            this.id = id;
            this.requestAt = requestAt;
        }
    }
}
