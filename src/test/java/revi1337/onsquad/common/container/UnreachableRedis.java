package revi1337.onsquad.common.container;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public final class UnreachableRedis {
    private static final String UNREACHABLE_HOST = "127.0.0.1";
    private static final int UNREACHABLE_PORT = 54321;

    private UnreachableRedis() {
    }

    public static StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(SharedConnectionFactory.INSTANCE);
    }

    private static final class SharedConnectionFactory {
        private static final LettuceConnectionFactory INSTANCE = create();

        private static LettuceConnectionFactory create() {
            LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
                    new RedisStandaloneConfiguration(UNREACHABLE_HOST, UNREACHABLE_PORT)
            );
            connectionFactory.afterPropertiesSet();
            Runtime.getRuntime().addShutdownHook(new Thread(connectionFactory::destroy));
            return connectionFactory;
        }
    }
}
