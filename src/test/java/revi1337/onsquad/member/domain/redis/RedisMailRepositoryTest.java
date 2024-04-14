package revi1337.onsquad.member.domain.redis;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import revi1337.onsquad.common.mail.MailStatus;
import revi1337.onsquad.config.SpringActiveProfilesResolver;
import revi1337.onsquad.support.TestContainerSupport;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RedisMailRepository 테스트")
@Import(RedisMailRepository.class)
@DataRedisTest
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
class RedisMailRepositoryTest extends TestContainerSupport {

    @Autowired private StringRedisTemplate stringRedisTemplate;
    @Autowired private RedisMailRepository redisMailRepository;

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_AUTH_CODE = "1111";
    private static final String TEST_KEY_FORMAT = "onsquad:auth:mail:%s";
    private static final String TEST_KEY_ENTRY = String.format(TEST_KEY_FORMAT, TEST_EMAIL);

    @AfterEach
    void tearDown() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @DisplayName("Key Value 가 정상적으로 저장되는지 확인한다.")
    @Test
    public void saveAuthCode() {
        // given
        Duration minutes = Duration.ofMinutes(3);
        stringRedisTemplate.opsForValue().set(TEST_KEY_ENTRY, TEST_AUTH_CODE, minutes);

        // when
        String authCode = stringRedisTemplate.opsForValue().get(TEST_KEY_ENTRY);

        // then
        assertThat(authCode).isEqualTo(TEST_AUTH_CODE);
    }

    @DisplayName("인증코드가 일치하면 true 를 반환한다.")
    @Test
    public void isValidMailAuthCode() {
        // given
        Duration minutes = Duration.ofMinutes(3);
        stringRedisTemplate.opsForValue().set(TEST_KEY_ENTRY, TEST_AUTH_CODE, minutes);

        // when
        boolean valid = redisMailRepository
                .isValidMailAuthCode(TEST_EMAIL, TEST_AUTH_CODE);

        // then
        assertThat(valid).isTrue();
    }

    @DisplayName("인증코드가 일치하지 않으면 false 를 반환한다.")
    @Test
    public void isValidMailAuthCode2() {
        // given
        Duration minutes = Duration.ofMinutes(3);
        stringRedisTemplate.opsForValue().set(TEST_KEY_ENTRY, TEST_AUTH_CODE, minutes);

        // when
        boolean valid = redisMailRepository
                .isValidMailAuthCode(TEST_EMAIL, "another_authcode");

        // then
        assertThat(valid).isFalse();
    }

    @DisplayName("인증코드 검증에 성공하면 값을 Overwrite 한다.")
    @Test
    public void overwriteAuthCodeToStatus() {
        // given
        Duration minutes = Duration.ofMinutes(3);
        stringRedisTemplate.opsForValue().set(TEST_KEY_ENTRY, TEST_AUTH_CODE, minutes);

        // when
        boolean canOverwrite = redisMailRepository
                .overwriteAuthCodeToStatus(TEST_EMAIL, MailStatus.SUCCESS, minutes);

        // then
        assertThat(canOverwrite).isTrue();
    }

    @DisplayName("인증코드 검증에 성공하지 못하면 값을 Overwrite 하지 못한다.")
    @Test
    public void overwriteAuthCodeToStatus2() {
        // given
        Duration minutes = Duration.ofMinutes(3);
        stringRedisTemplate.opsForValue().set(TEST_KEY_ENTRY, TEST_AUTH_CODE, minutes);

        // when
        boolean canOverwrite = redisMailRepository
                .overwriteAuthCodeToStatus("another_email", MailStatus.SUCCESS, minutes);

        // then
        assertThat(canOverwrite).isFalse();
    }

    @DisplayName("이메일 인증이 진행되어있는 상태면 200 을 반환한다.")
    @Test
    public void isValidMailStatus() {
        // given
        Duration minutes = Duration.ofMinutes(3);
        MailStatus mailStatus = MailStatus.SUCCESS;
        stringRedisTemplate.opsForValue().set(TEST_KEY_ENTRY, mailStatus.getText(), minutes);

        // when
        boolean isValidMailStatus = redisMailRepository.isValidMailStatus(TEST_EMAIL, mailStatus);

        // then
        assertThat(isValidMailStatus).isTrue();
    }

    @DisplayName("이메일 인증이 진행되어있지 않은 상태면 false 를 반환한다.")
    @Test
    public void isValidMailStatus2() {
        // given
        Duration minutes = Duration.ofMinutes(3);
        MailStatus mailStatus = MailStatus.SUCCESS;
        stringRedisTemplate.opsForValue().set(TEST_KEY_ENTRY, "FAIL", minutes);

        // when
        boolean isValidMailStatus = redisMailRepository.isValidMailStatus(TEST_EMAIL, mailStatus);

        // then
        assertThat(isValidMailStatus).isFalse();
    }
}
