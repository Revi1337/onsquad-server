package revi1337.onsquad.squad_member.application.dto;

import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoDto;
import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;

import java.util.List;

public record EnrolledSquadDto(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberInfoDto crewOwner,
        List<SimpleSquadInfoDto> squads
) {
    public static EnrolledSquadDto from(EnrolledSquadDomainDto squadDomainDto) {
        return new EnrolledSquadDto(
                squadDomainDto.crewId(),
                squadDomainDto.crewName().getValue(),
                squadDomainDto.imageUrl(),
                SimpleMemberInfoDto.from(squadDomainDto.crewOwner()),
                squadDomainDto.squads().stream()
                        .map(SimpleSquadInfoDto::from)
                        .toList()
        );
    }
}





//package revi1337.onsquad.squad_member.application.dto;
//
//import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
//import revi1337.onsquad.squad.application.dto.SquadInfoDto;
//import revi1337.onsquad.squad_member.domain.dto.EnrolledSquadDomainDto;
//
//import java.util.List;
//
//public record EnrolledSquadDto(
//        Long crewId,
//        String crewName,
//        String imageUrl,
//        SimpleMemberInfoDto crewOwner,
//        List<SquadInfoDto> squads
//) {
//    public static EnrolledSquadDto from(EnrolledSquadDomainDto squadDomainDto) {
//        return new EnrolledSquadDto(
//                squadDomainDto.crewId(),
//                squadDomainDto.crewName().getValue(),
//                squadDomainDto.imageUrl(),
//                SimpleMemberInfoDto.from(squadDomainDto.crewOwner()),
//                squadDomainDto.squads().stream()
//                        .map(SquadInfoDto::from)
//                        .toList()
//        );
//    }
//}
