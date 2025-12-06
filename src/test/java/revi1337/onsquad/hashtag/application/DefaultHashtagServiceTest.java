package revi1337.onsquad.hashtag.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;

@Sql({"/h2-hashtag.sql"})
class DefaultHashtagServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private DefaultHashtagService defaultHashtagService;

    @Test
    @DisplayName("크루 해시태그 종류들을 검증한다.")
    void success() {
        List<String> hashtags = defaultHashtagService.findHashtags();

        assertThat(hashtags).containsExactlyInAnyOrder(HashtagType.unmodifiableList()
                .stream().map(HashtagType::getText)
                .toArray(String[]::new));
    }
}
