package revi1337.onsquad.crew.application.dto;

import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.member.dto.SimpleMemberInfoDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record CrewInfoDto(
        Long id,
        String name,
        String introduce,
        String detail,
        String imageUrl,
        String kakaoLink,
        List<Object> hashTags,
        SimpleMemberInfoDto crewOwner
) {
    public static CrewInfoDto from(CrewInfoDomainDto crewInfoDomainDto) {
        ArrayList<Object> hashTags = new ArrayList<>();
        hashTags.add(crewInfoDomainDto.memberCnt());
        if (!crewInfoDomainDto.hashTags().getValue().equals("[EMPTY]")) {
            hashTags.addAll(Arrays.asList(crewInfoDomainDto.hashTags().getValue().split(",")));
        }

        return new CrewInfoDto(
                crewInfoDomainDto.id(),
                crewInfoDomainDto.name().getValue(),
                crewInfoDomainDto.introduce().getValue(),
                crewInfoDomainDto.detail() != null ? crewInfoDomainDto.detail().getValue() : null,
                crewInfoDomainDto.imageUrl(),
                crewInfoDomainDto.kakaoLink(),
                hashTags,
                SimpleMemberInfoDto.from(crewInfoDomainDto.crewOwner())
        );
    }
}
