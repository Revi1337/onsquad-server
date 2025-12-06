package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;
import revi1337.onsquad.squad.domain.entity.vo.Title;

public record MySquadRequestDomainDto(
        Long id,
        Name name,
        String imageUrl,
        SimpleMemberDomainDto owner,
        List<SquadWithParticipant> squads
) {

    @QueryProjection
    public MySquadRequestDomainDto(
            Long id,
            Name name,
            String imageUrl,
            SimpleMemberDomainDto owner,
            List<SquadWithParticipant> squads
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.owner = owner;
        this.squads = squads;
    }

    public record SquadWithParticipant(
            Long id,
            Title title,
            int capacity,
            int remain,
            List<CategoryType> categories,
            SimpleMemberDomainDto owner,
            RequestParticipantDomainDto request
    ) {

        @QueryProjection
        public SquadWithParticipant(
                Long id,
                Title title,
                int capacity,
                int remain,
                SimpleMemberDomainDto squadOwner,
                RequestParticipantDomainDto request
        ) {
            this(id, title, capacity, remain, new ArrayList<>(), squadOwner, request);
        }

        @QueryProjection
        public SquadWithParticipant(
                Long id,
                Title title,
                int capacity,
                int remain,
                List<CategoryType> categories,
                SimpleMemberDomainDto owner,
                RequestParticipantDomainDto request
        ) {
            this.id = id;
            this.title = title;
            this.capacity = capacity;
            this.remain = remain;
            this.categories = categories;
            this.owner = owner;
            this.request = request;
        }

        public record RequestParticipantDomainDto(
                Long id,
                LocalDateTime requestAt
        ) {

            @QueryProjection
            public RequestParticipantDomainDto(
                    Long id,
                    LocalDateTime requestAt
            ) {
                this.id = id;
                this.requestAt = requestAt;
            }
        }
    }
}
