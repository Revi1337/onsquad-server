package revi1337.onsquad.squad_member.application.response;

import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_member.domain.result.EnrolledSquadResult;
import revi1337.onsquad.squad_member.domain.result.EnrolledSquadResult.SimpleSquadInfoDomainDto;

public record EnrolledSquadResponse(
        Long id,
        String name,
        String imageUrl,
        SimpleMemberDto owner,
        List<SimpleSquadInfoDto> squads
) {

    public static EnrolledSquadResponse from(EnrolledSquadResult squadDomainDto) {
        return new EnrolledSquadResponse(
                squadDomainDto.id(),
                squadDomainDto.name().getValue(),
                squadDomainDto.imageUrl(),
                SimpleMemberDto.from(squadDomainDto.owner()),
                squadDomainDto.squads().stream()
                        .map(SimpleSquadInfoDto::from)
                        .toList()
        );
    }

    public record SimpleSquadInfoDto(
            Long id,
            String title,
            int capacity,
            int remain,
            Boolean isLeader,
            List<String> categories,
            SimpleMemberDto leader
    ) {

        public static SimpleSquadInfoDto from(SimpleSquadInfoDomainDto simpleSquadInfoDomainDto) {
            return new SimpleSquadInfoDto(
                    simpleSquadInfoDomainDto.id(),
                    simpleSquadInfoDomainDto.title().getValue(),
                    simpleSquadInfoDomainDto.capacity(),
                    simpleSquadInfoDomainDto.capacity(),
                    simpleSquadInfoDomainDto.isLeader(),
                    simpleSquadInfoDomainDto.categories().stream()
                            .map(CategoryType::getText)
                            .toList(),
                    SimpleMemberDto.from(simpleSquadInfoDomainDto.squadOwner())
            );
        }
    }
}
