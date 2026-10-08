package revi1337.onsquad.common.container;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 아무도 수신 대기하지 않는 포트를 바라보는 Redis 클라이언트를 제공하여, 컨테이너 없이 Redis 연결 실패를 재현한다.
 * <p>
 * {@link LettuceConnectionFactory} 는 생성마다 자체 ClientResources(스레드/이벤트 루프)를 만들기 때문에, 테스트마다 새로 만들면 누수되어 전체 스위트에서 힙이 고갈된다.
 * 따라서 JVM 당 하나만 만들어 공유하고 종료 시 정리한다.
 */
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
