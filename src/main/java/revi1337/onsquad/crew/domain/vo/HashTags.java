package revi1337.onsquad.crew.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.lang.reflect.Array;
import java.util.*;

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
            throw new IllegalArgumentException( // TODO 커스텀 익셉션 필요.
                    String.format("해시태그의 최대 개수는 %d 개 입니다.", MAX_LENGTH)
            );
        }
    }

    public HashTags updateHashTags(Collection<String> hashTags) {
        return new HashTags(hashTags);
    }
}