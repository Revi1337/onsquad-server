package revi1337.onsquad.crew.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewResponse(
        Long id,
        String name,
        String introduce,
        String detail,
        String imageUrl,
        String kakaoLink,
        List<String> hashtagTypes,
        Long memberCnt,
        SimpleMemberDto owner,
        Boolean alreadyJoin
) {

    public static CrewResponse from(CrewResult crewResult) {
        return new CrewResponse(
                crewResult.getId(),
                crewResult.getName().getValue(),
                crewResult.getIntroduce().getValue(),
                crewResult.getDetail() != null ? crewResult.getDetail().getValue() : null,
                crewResult.getImageUrl() != null ? crewResult.getImageUrl() : "",
                crewResult.getKakaoLink(),
                crewResult.getHashtagTypes().stream()
                        .map(HashtagType::getText)
                        .toList(),
                crewResult.getMemberCnt(),
                SimpleMemberDto.from(crewResult.getCrewOwner()),
                null
        );
    }
}
