package revi1337.onsquad.token.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_1;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_2;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.TestContainerSupport;
import revi1337.onsquad.token.domain.model.RefreshToken;

@ImportAutoConfiguration(RedisAutoConfiguration.class)
@ContextConfiguration(classes = RedisHashTokenRepository.class)
@ExtendWith(SpringExtension.class)
class RedisHashTokenRepositoryTest extends TestContainerSupport {

    @Autowired
    private RedisHashTokenRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Test
    @DisplayName("RefreshToken 저장에 성공한다.")
    void save1() {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofSeconds(2);

        repository.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        assertThat(repository.findBy(DUMMY_MEMBER_ID)).isPresent();
    }

    @Test
    @DisplayName("만료 시간이 지나면 RefreshToken 는 사라진다.")
    void save2() {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofNanos(1);

        repository.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        assertThat(repository.findBy(DUMMY_MEMBER_ID)).isEmpty();
    }

    @Test
    @DisplayName("RefreshToken 조회에 성공한다.")
    void findBy() {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofSeconds(2);
        repository.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        Optional<RefreshToken> OPTIONAL_TOKEN = repository.findBy(DUMMY_MEMBER_ID);

        assertThat(OPTIONAL_TOKEN).isPresent();
    }

    @Test
    @DisplayName("RefreshToken 삭제에 성공한다.")
    void deleteBy() {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofSeconds(2);
        repository.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        repository.deleteBy(DUMMY_MEMBER_ID);

        assertThat(repository.findBy(DUMMY_MEMBER_ID)).isEmpty();
    }

    @Test
    @DisplayName("모든 RefreshToken 삭제에 성공한다.")
    void deleteAll() {
        Long DUMMY_MEMBER_ID_1 = 1L;
        Duration EXPIRE_1 = Duration.ofSeconds(2);
        repository.save(REFRESH_TOKEN_1, DUMMY_MEMBER_ID_1, EXPIRE_1);
        Long DUMMY_MEMBER_ID_2 = 2L;
        Duration EXPIRE_2 = Duration.ofSeconds(3);
        repository.save(REFRESH_TOKEN_2, DUMMY_MEMBER_ID_2, EXPIRE_2);

        repository.deleteAll();

        assertThat(repository.findBy(DUMMY_MEMBER_ID_1)).isEmpty();
        assertThat(repository.findBy(DUMMY_MEMBER_ID_2)).isEmpty();
    }
}
