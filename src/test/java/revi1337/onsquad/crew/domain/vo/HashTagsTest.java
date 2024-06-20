package revi1337.onsquad.crew.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("해시태그 vo 테스트")
class HashTagsTest {

    @Test
    @DisplayName("해시태그의 개수가 10 개가 넘어가면 실패한다.")
    public void hashtagTest() {
        // given
        List<String> hashtags = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");

        // when & then
        assertThatThrownBy(() -> new HashTags(hashtags))
                .isInstanceOf(CrewDomainException.InvalidHashTagsSize.class)
                .hasMessage("해시태그의 최대 개수는 10 개 입니다.");
    }

    @Test
    @DisplayName("유니크한 해시태그의 개수가 10 개 이하면 성공한다.")
    public void hashtagTest2() {
        // given
        List<String> hashtags = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "10", "10");

        // when
        HashTags hashTags = new HashTags(hashtags);

        // then
        assertThat(hashTags).isNotNull();
    }

    @Test
    @DisplayName("해시태그에 null 이 들어가도 성공해야 한다.")
    public void hashtagTest3() {
        // given
        List<String> hashtags = null;

        // when
        HashTags hashTags = new HashTags(hashtags);

        // then
        assertThat(hashTags).isNotNull();
    }
}