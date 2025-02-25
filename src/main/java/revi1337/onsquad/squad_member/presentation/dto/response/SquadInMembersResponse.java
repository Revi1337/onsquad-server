package revi1337.onsquad.squad_member.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import revi1337.onsquad.squad_member.application.dto.SquadInMembersDto;

@JsonInclude(Include.NON_NULL)
public record SquadInMembersResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        boolean isOwner,
        List<String> categories,
        List<SquadMemberResponse> members
) {
    public static SquadInMembersResponse from(SquadInMembersDto squadInMembersDto) {
        return new SquadInMembersResponse(
                squadInMembersDto.id(),
                squadInMembersDto.title(),
                squadInMembersDto.capacity(),
                squadInMembersDto.remain(),
                squadInMembersDto.isOwner(),
                squadInMembersDto.categories(),
                squadInMembersDto.members().stream()
                        .map(SquadMemberResponse::from)
                        .toList()
        );
    }
}

//package revi1337.onsquad.squad_member.presentation.dto.response;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonInclude.Include;
//import java.util.List;
//import revi1337.onsquad.squad_member.application.dto.SquadInMembersDto;
//
//@JsonInclude(Include.NON_NULL)
//public record SquadInMembersResponse(
//        Long id,
//        String title,
//        int capacity,
//        int remain,
//        boolean isOwner,
//        List<String> categories,
//        List<SquadMemberResponse> members
//) {
//    public static SquadInMembersResponse from(SquadInMembersDto squadInMembersDto) {
//        return new SquadInMembersResponse(
//                squadInMembersDto.id(),
//                squadInMembersDto.title(),
//                squadInMembersDto.capacity(),
//                squadInMembersDto.remain(),
//                squadInMembersDto.isOwner(),
//                squadInMembersDto.categories(),
//                squadInMembersDto.members().stream()
//                        .map(SquadMemberResponse::from)
//                        .toList()
//        );
//    }
//}
