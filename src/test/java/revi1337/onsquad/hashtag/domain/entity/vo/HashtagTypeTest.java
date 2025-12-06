package revi1337.onsquad.hashtag.domain.entity.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashtagTypeTest {

    @Test
    @DisplayName("해시태그 value 로 Enum 을 가져온다.")
    void hashtag() {
        String hashtagString = HashtagType.ACTIVE.getText();

        HashtagType hashtagType = HashtagType.fromText(hashtagString);

        assertThat(hashtagType).isSameAs(HashtagType.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 해시태그 value 로 Enum 을 가져오려하면 null 을 반환한다.")
    void hashtag1() {
        String hashtagString = "invalid_hashtag";

        HashtagType hashtagType = HashtagType.fromText(hashtagString);

        assertThat(hashtagType).isNull();
    }

    @Test
    @DisplayName("해시태그 value 들로 Enum 들을 가져온다.")
    void hashtag2() {
        List<String> hashtagStrings = List.of(HashtagType.ACTIVE.getText(), HashtagType.MOVIE.getText());

        List<HashtagType> hashtagTypes = HashtagType.fromTexts(hashtagStrings);

        assertThat(hashtagTypes).containsExactly(HashtagType.ACTIVE, HashtagType.MOVIE);
    }

    @Test
    @DisplayName("해시태그 value 들로 Enum 들을 가져온다. (중복제거까지)")
    void hashtag3() {
        List<String> hashtagStrings = List.of(
                HashtagType.ACTIVE.getText(), HashtagType.MOVIE.getText(), HashtagType.MOVIE.getText()
        );

        List<HashtagType> hashtagTypes = HashtagType.fromTexts(hashtagStrings);

        assertThat(hashtagTypes).containsExactly(HashtagType.ACTIVE, HashtagType.MOVIE);
    }
}
