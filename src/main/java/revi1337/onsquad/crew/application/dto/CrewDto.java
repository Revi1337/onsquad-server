package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

public record CrewDto(
        Long id,
        String name,
        String introduce,
        String detail,
        String imageUrl,
        String kakaoLink,
        List<String> hashtagTypes,
        Long memberCnt,
        SimpleMemberInfoDto owner,
        Boolean alreadyJoin
) {
    public static CrewDto from(CrewDomainDto crewDomainDto) {
        return new CrewDto(
                crewDomainDto.getId(),
                crewDomainDto.getName().getValue(),
                crewDomainDto.getIntroduce().getValue(),
                crewDomainDto.getDetail() != null ? crewDomainDto.getDetail().getValue() : null,
                crewDomainDto.getImageUrl() != null ? crewDomainDto.getImageUrl() : "",
                crewDomainDto.getKakaoLink(),
                crewDomainDto.getHashtagTypes().stream()
                        .map(HashtagType::getText)
                        .toList(),
                crewDomainDto.getMemberCnt(),
                SimpleMemberInfoDto.from(crewDomainDto.getCrewOwner()),
                null
        );
    }
}
