package revi1337.onsquad.hashtag.util;

import static revi1337.onsquad.crew.error.CrewErrorCode.INVALID_HASHTAG;

import java.util.List;
import java.util.stream.Collectors;
import revi1337.onsquad.crew.error.CrewDomainException;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;

public abstract class HashtagTypeUtil {

    public static List<HashtagType> extractPossible(List<HashtagType> hashtagTypes) {
        return hashtagTypes.stream()
                .peek(HashtagTypeUtil::validateHashtag)
                .collect(Collectors.toList());
    }

    public static void validateHashtag(HashtagType hashtagType) {
        if (hashtagType == null) {
            throw new CrewDomainException.InvalidHashtag(INVALID_HASHTAG);
        }
    }
}
