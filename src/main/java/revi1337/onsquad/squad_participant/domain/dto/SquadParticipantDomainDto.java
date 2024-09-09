package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.squad_category.domain.dto.SquadCategoryDomainDto;

import java.time.LocalDateTime;
import java.util.List;

public record SquadParticipantDomainDto(
        Long crewId,
        Long id,
        Title title,
        Capacity capacity,
        Address address,
        String kakaoLink,
        String discordLink,
        List<SquadCategoryDomainDto> categories,
        SimpleMemberInfoDomainDto squadOwner,
        RequestParticipantDomainDto request
) {
    @QueryProjection
    public SquadParticipantDomainDto(Long crewId, Long id, Title title, Capacity capacity, Address address, String kakaoLink, String discordLink, List<SquadCategoryDomainDto> categories, SimpleMemberInfoDomainDto squadOwner, RequestParticipantDomainDto request) {
        this.crewId = crewId;
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.address = address;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
        this.categories = categories;
        this.squadOwner = squadOwner;
        this.request = request;
    }

    public SquadParticipantDomainDto(RequestParticipantDomainDto request) {
        this(null, null, null, null, null, null, null, null, null, request);
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



//package revi1337.onsquad.squad_participant.domain.dto;
//
//import com.querydsl.core.annotations.QueryProjection;
//import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
//
//import java.time.LocalDateTime;
//
//public record SquadParticipantDomainDto(
//        Long id,
//        LocalDateTime requestAt,
//        SquadInfoDomainDto squadInfo
//) {
//    @QueryProjection
//    public SquadParticipantDomainDto(Long id, LocalDateTime requestAt, SquadInfoDomainDto squadInfo) {
//        this.id = id;
//        this.requestAt = requestAt;
//        this.squadInfo = squadInfo;
//    }
//}
