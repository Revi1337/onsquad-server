package revi1337.onsquad.crew.domain.entity.vo;

import static revi1337.onsquad.crew.error.CrewErrorCode.INVALID_HASHTAGS_SIZE;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class HashTags {

    private static final String HASHTAG_DELIMITER = ",";
    private static final String DEFAULT_HASHTAG = "[EMPTY]";
    private static final int MAX_LENGTH = 10;

    @Column(name = "hashtags", nullable = false)
    private String value;

    public HashTags(Collection<String> hashtags) {
        if (hashtags == null) {
            this.value = DEFAULT_HASHTAG;
            return;
        }

        validateSize(hashtags);
        List<String> hashTags = new ArrayList<>(new LinkedHashSet<>(hashtags));
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
