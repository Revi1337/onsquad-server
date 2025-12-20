package revi1337.onsquad.crew.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew_hashtag.domain.entity.CrewHashtag;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
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
        List<String> hashtags,
        Long memberCount,
        SimpleMemberDto owner
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
                SimpleMemberDto.from(crewResult.getCrewOwner())
        );
    }

    public static CrewResponse from(Crew crew) {
        return new CrewResponse(
                crew.getId(),
                crew.getName().getValue(),
                crew.getIntroduce().getValue(),
                crew.getDetail() != null ? crew.getDetail().getValue() : null,
                crew.getImageUrl() != null ? crew.getImageUrl() : "",
                crew.getKakaoLink(),
                crew.getHashtags().stream()
                        .map(CrewHashtag::getHashtag)
                        .map(Hashtag::getHashtagType)
                        .map(HashtagType::getText)
                        .toList(),
                crew.getCurrentSize(),
                SimpleMemberDto.from(crew.getMember())
        );
    }
}
