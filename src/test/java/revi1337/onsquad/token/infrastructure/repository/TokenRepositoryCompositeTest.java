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
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.token.domain.model.RefreshToken;

class TokenRepositoryCompositeTest extends ApplicationLayerWithTestContainerSupport {

    @Autowired
    private TokenRepositoryComposite repositoryComposite;

    @Autowired
    private RedisHashTokenRepository redisHashTokenRepository;

    @Autowired
    private ExpiringMapTokenRepository expiringMapTokenRepository;

    @BeforeEach
    void setUp() {
        redisHashTokenRepository.deleteAll();
        expiringMapTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("RefreshToken 저장에 성공한다.")
    void save1() {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofSeconds(2);

        repositoryComposite.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        assertThat(expiringMapTokenRepository.findBy(DUMMY_MEMBER_ID)).isPresent();
        assertThat(redisHashTokenRepository.findBy(DUMMY_MEMBER_ID)).isPresent();
    }

    @Test
    @DisplayName("RefreshToken 조회에 성공한다.")
    void findBy() {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofSeconds(2);
        repositoryComposite.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        Optional<RefreshToken> OPTIONAL_TOKEN = repositoryComposite.findBy(DUMMY_MEMBER_ID);

        assertThat(OPTIONAL_TOKEN).isPresent();
        assertThat(expiringMapTokenRepository.findBy(DUMMY_MEMBER_ID)).isPresent();
        assertThat(redisHashTokenRepository.findBy(DUMMY_MEMBER_ID)).isPresent();
    }

    @Test
    @DisplayName("RefreshToken 삭제에 성공한다.")
    void deleteBy() {
        Long DUMMY_MEMBER_ID = 1L;
        Duration EXPIRE = Duration.ofSeconds(2);
        repositoryComposite.save(REFRESH_TOKEN, DUMMY_MEMBER_ID, EXPIRE);

        repositoryComposite.deleteBy(DUMMY_MEMBER_ID);

        assertThat(expiringMapTokenRepository.findBy(DUMMY_MEMBER_ID)).isEmpty();
        assertThat(redisHashTokenRepository.findBy(DUMMY_MEMBER_ID)).isEmpty();
        assertThat(repositoryComposite.findBy(DUMMY_MEMBER_ID)).isEmpty();
    }

    @Test
    @DisplayName("모든 RefreshToken 삭제에 성공한다.")
    void deleteAll() {
        Long DUMMY_MEMBER_ID_1 = 1L;
        Long DUMMY_MEMBER_ID_2 = 2L;
        Duration EXPIRE_1 = Duration.ofSeconds(2);
        Duration EXPIRE_2 = Duration.ofSeconds(3);
        repositoryComposite.save(REFRESH_TOKEN_1, DUMMY_MEMBER_ID_1, EXPIRE_1);
        repositoryComposite.save(REFRESH_TOKEN_2, DUMMY_MEMBER_ID_2, EXPIRE_2);

        repositoryComposite.deleteAll();

        assertThat(redisHashTokenRepository.findBy(DUMMY_MEMBER_ID_1)).isEmpty();
        assertThat(redisHashTokenRepository.findBy(DUMMY_MEMBER_ID_2)).isEmpty();
        assertThat(redisHashTokenRepository.findBy(DUMMY_MEMBER_ID_1)).isEmpty();
        assertThat(redisHashTokenRepository.findBy(DUMMY_MEMBER_ID_2)).isEmpty();
        assertThat(repositoryComposite.findBy(DUMMY_MEMBER_ID_1)).isEmpty();
    }
}
