package revi1337.onsquad.squad_category.application.initializer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils;

@ActiveProfiles("local")
@Import(SquadCategoryCacheEvictor.class)
@ImportAutoConfiguration(RedisAutoConfiguration.class)
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
@ExtendWith(SpringExtension.class)
class SquadCategoryCacheEvictorTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SquadCategoryCacheEvictor squadCategoryCacheEvictor;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("애플리케이션 구동 완료 시 스쿼드 카테고리 관련 모든 캐시를 삭제한다")
    void onApplicationEvent() {
        String squadCategoryKey1 = String.format(CacheFormat.SIMPLE, "squad:1:categories");
        String squadCategoryKey2 = String.format(CacheFormat.SIMPLE, "squad:2:categories");
        stringRedisTemplate.opsForValue().set(squadCategoryKey1, "value");
        stringRedisTemplate.opsForValue().set(squadCategoryKey2, "value");
        assertThat(stringRedisTemplate.hasKey(squadCategoryKey1)).isTrue();
        assertThat(stringRedisTemplate.hasKey(squadCategoryKey2)).isTrue();

        squadCategoryCacheEvictor.onApplicationEvent();

        assertThat(RedisScanUtils.scanKeys(stringRedisTemplate, String.format(CacheFormat.SIMPLE, "squad:*:categories")).size())
                .isEqualTo(0);
    }
}
