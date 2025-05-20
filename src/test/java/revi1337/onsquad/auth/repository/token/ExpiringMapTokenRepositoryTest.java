package revi1337.onsquad.auth.repository.token;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_1;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_2;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.application.token.model.RefreshToken;

class ExpiringMapTokenRepositoryTest {

    private final ExpiringMapTokenRepository repository = new ExpiringMapTokenRepository();
    private final Map<String, RefreshToken> container = (Map<String, RefreshToken>)
            ReflectionTestUtils.getField(ExpiringMapTokenRepository.class, "REFRESH_CACHE");

    @BeforeEach
    void setUp() {
        container.clear();
    }

    @Test
    @DisplayName("RefreshToken 저장에 성공한다.")
    void save1() {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofSeconds(2);

        repository.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        assertThat(container).hasSize(1);
    }

    @Test
    @DisplayName("만료 시간이 지나면 RefreshToken 는 사라진다.")
    void save2() throws InterruptedException {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofNanos(1);

        repository.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        Thread.sleep(50);
        assertThat(container).hasSize(0);
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

        assertThat(container).hasSize(0);
    }
}
