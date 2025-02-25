package revi1337.onsquad.squad_member.application.dto;

import java.util.List;
import java.util.stream.Collectors;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad_member.domain.dto.SquadInMembersDomainDto;

public record SquadInMembersDto(
        Long id,
        String title,
        int capacity,
        int remain,
        boolean isOwner,
        List<String> categories,
        List<SquadMemberDto> members
) {
    public static SquadInMembersDto from(SquadInMembersDomainDto squadInMembersDomainDto) {
        return new SquadInMembersDto(
                squadInMembersDomainDto.getId(),
                squadInMembersDomainDto.getTitle().getValue(),
                squadInMembersDomainDto.getCapacity().getValue(),
                squadInMembersDomainDto.getCapacity().getRemain(),
                squadInMembersDomainDto.getIsOwner(),
                squadInMembersDomainDto.getCategories().stream()
                        .map(CategoryType::getText)
                        .collect(Collectors.toList()),
                squadInMembersDomainDto.getMembers().stream()
                        .map(SquadMemberDto::from)
                        .toList()
        );
    }

    public static SquadInMembersDto from(Long memberId, SquadInMembersDomainDto squadInMembersDomainDto) {
        return new SquadInMembersDto(
                squadInMembersDomainDto.getId(),
                squadInMembersDomainDto.getTitle().getValue(),
                squadInMembersDomainDto.getCapacity().getValue(),
                squadInMembersDomainDto.getCapacity().getRemain(),
                squadInMembersDomainDto.getIsOwner(),
                squadInMembersDomainDto.getCategories().stream()
                        .map(CategoryType::getText)
                        .collect(Collectors.toList()),
                squadInMembersDomainDto.getMembers().stream()
                        .map(squadMember -> SquadMemberDto.from(memberId, squadMember))
                        .toList()
        );
    }
}

//package revi1337.onsquad.squad_member.application.dto;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import revi1337.onsquad.category.domain.vo.CategoryType;
//import revi1337.onsquad.squad_member.domain.dto.SquadInMembersDomainDto;
//
//public record SquadInMembersDto(
//        Long id,
//        String title,
//        int capacity,
//        int remain,
//        boolean isOwner,
//        List<String> categories,
//        List<SquadMemberDto> members
//) {
//    public static SquadInMembersDto from(SquadInMembersDomainDto squadInMembersDomainDto) {
//        return new SquadInMembersDto(
//                squadInMembersDomainDto.id(),
//                squadInMembersDomainDto.title().getValue(),
//                squadInMembersDomainDto.capacity().getValue(),
//                squadInMembersDomainDto.capacity().getRemain(),
//                squadInMembersDomainDto.isOwner(),
//                squadInMembersDomainDto.categories().stream()
//                        .map(CategoryType::getText)
//                        .collect(Collectors.toList()),
//                squadInMembersDomainDto.members().stream()
//                        .map(SquadMemberDto::from)
//                        .toList()
//        );
//    }
//}
