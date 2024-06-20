package revi1337.onsquad.crew.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

import java.util.*;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class HashTags {

    private static final String HASHTAG_DELIMITER = ",";
    private static final int MAX_LENGTH = 10;

    @Column(name = "hashtags")
    private String value;

    public HashTags(Collection<String> hashtags) {
        if (hashtags == null) {
            return;
        }

        validateSize(hashtags);

        List<String> hashTags = new ArrayList<>(new HashSet<>(hashtags));
        Collections.reverse(hashTags);
        this.value = String.join(HASHTAG_DELIMITER, hashTags);
    }

    public void validateSize(Collection<String> hashtags) {
        HashSet<String> hashtagsSet = new HashSet<>(hashtags);
        if (hashtagsSet.isEmpty() || hashtagsSet.size() > MAX_LENGTH) {
            throw new CrewDomainException.InvalidHashTagsSize(INVALID_HASHTAGS_SIZE, MAX_LENGTH);
        }
    }

    public HashTags updateHashTags(Collection<String> hashTags) {
        return new HashTags(hashTags);
    }
}