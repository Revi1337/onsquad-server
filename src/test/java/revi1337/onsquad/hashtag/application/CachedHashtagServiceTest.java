package revi1337.onsquad.hashtag.application;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.ApplicationLayerTestSupport;

@Sql({"/h2-hashtag.sql"})
class CachedHashtagServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private CachedHashtagService cachedHashtagService;

    @SpyBean
    private DefaultHashtagService defaultHashtagService;

    @Test
    @DisplayName("쿼리가 나간 후 캐싱되면, 그 뒤로는 호출되지 않는다.")
    void success() {
        // given
        reset(defaultHashtagService);
        cachedHashtagService.findHashtags();

        // when
        cachedHashtagService.findHashtags();

        // then
        verify(defaultHashtagService, times(1)).findHashtags();
    }
}