package revi1337.onsquad.squad_participant.application.dto;

import revi1337.onsquad.member.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_category.application.dto.SquadCategoryDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantDomainDto;

import java.time.LocalDateTime;
import java.util.List;

public record SquadParticipantDto(
        Long id,
        String title,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        List<SquadCategoryDto> categories,
        SimpleMemberInfoDto squadOwner,
        RequestParticipantDto request
) {
    public static SquadParticipantDto from(SquadParticipantDomainDto squadParticipantDomainDto) {
        return new SquadParticipantDto(
                squadParticipantDomainDto.id(),
                squadParticipantDomainDto.title().getValue(),
                squadParticipantDomainDto.capacity().getValue(),
                squadParticipantDomainDto.capacity().getRemain(),
                squadParticipantDomainDto.address().getValue(),
                squadParticipantDomainDto.address().getDetail(),
                squadParticipantDomainDto.kakaoLink(),
                squadParticipantDomainDto.discordLink(),
                squadParticipantDomainDto.categories().stream()
                        .map(SquadCategoryDto::from)
                        .toList(),
                SimpleMemberInfoDto.from(squadParticipantDomainDto.squadOwner()),
                RequestParticipantDto.from(squadParticipantDomainDto.request())
        );
    }

    public record RequestParticipantDto(
            Long id,
            LocalDateTime requestAt
    ) {
        public static RequestParticipantDto from(SquadParticipantDomainDto.RequestParticipantDomainDto requestParticipantDomainDto) {
            return new RequestParticipantDto(
                    requestParticipantDomainDto.id(),
                    requestParticipantDomainDto.requestAt()
            );
        }
    }
}




//package revi1337.onsquad.squad_participant.application.dto;
//
//import revi1337.onsquad.squad.application.dto.SquadInfoDto;
//import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantDomainDto;
//
//import java.time.LocalDateTime;
//
//public record SquadParticipantDto(
//        Long id,
//        LocalDateTime requestAt,
//        SquadInfoDto squadInfo
//) {
//    public static SquadParticipantDto from(SquadParticipantDomainDto squadParticipantDomainDto) {
//        return new SquadParticipantDto(
//                squadParticipantDomainDto.id(),
//                squadParticipantDomainDto.requestAt(),
//                SquadInfoDto.from(squadParticipantDomainDto.squadInfo())
//        );
//    }
//}
