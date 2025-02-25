package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

public record CrewInfoDto(
        Long id,
        String name,
        String introduce,
        String detail,
        String imageUrl,
        String kakaoLink,
        List<String> hashtagTypes,
        Long memberCnt,
        SimpleMemberInfoDto crewOwner,
        Boolean alreadyJoin
) {
    public static CrewInfoDto from(CrewInfoDomainDto crewInfoDomainDto) {
        return new CrewInfoDto(
                crewInfoDomainDto.getId(),
                crewInfoDomainDto.getName().getValue(),
                crewInfoDomainDto.getIntroduce().getValue(),
                crewInfoDomainDto.getDetail() != null ? crewInfoDomainDto.getDetail().getValue() : null,
                crewInfoDomainDto.getImageUrl() != null ? crewInfoDomainDto.getImageUrl() : "",
                crewInfoDomainDto.getKakaoLink(),
                crewInfoDomainDto.getHashtagTypes().stream()
                        .map(HashtagType::getText)
                        .toList(),
                crewInfoDomainDto.getMemberCnt(),
                SimpleMemberInfoDto.from(crewInfoDomainDto.getCrewOwner()),
                null
        );
    }

    public static CrewInfoDto from(Boolean alreadyJoin, CrewInfoDomainDto crewInfoDomainDto) {
        return new CrewInfoDto(
                crewInfoDomainDto.getId(),
                crewInfoDomainDto.getName().getValue(),
                crewInfoDomainDto.getIntroduce().getValue(),
                crewInfoDomainDto.getDetail() != null ? crewInfoDomainDto.getDetail().getValue() : null,
                crewInfoDomainDto.getImageUrl() != null ? crewInfoDomainDto.getImageUrl() : "",
                crewInfoDomainDto.getKakaoLink(),
                crewInfoDomainDto.getHashtagTypes().stream()
                        .map(HashtagType::getText)
                        .toList(),
                crewInfoDomainDto.getMemberCnt(),
                SimpleMemberInfoDto.from(crewInfoDomainDto.getCrewOwner()),
                alreadyJoin
        );
    }
}
