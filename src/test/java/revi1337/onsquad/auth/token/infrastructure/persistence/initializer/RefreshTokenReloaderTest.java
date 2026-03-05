package revi1337.onsquad.auth.token.infrastructure.persistence.initializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;
import revi1337.onsquad.auth.token.domain.model.RefreshTokens;
import revi1337.onsquad.auth.token.infrastructure.persistence.ExpiringMapRefreshTokenStorage;
import revi1337.onsquad.common.config.web.ObjectMapperConfig;

@ActiveProfiles("local")
@TestPropertySource(properties = "onsquad.token.refresh-token.backup-path=dummy-path")
@ContextConfiguration(classes = {RefreshTokenReloader.class, ExpiringMapRefreshTokenStorage.class, ObjectMapperConfig.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class RefreshTokenReloaderTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExpiringMapRefreshTokenStorage expiringMapRefreshTokenStorage;

    @Autowired
    private RefreshTokenReloader refreshTokenReloader;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        ExpiringMap<String, RefreshToken> refreshStore = (ExpiringMap<String, RefreshToken>) ReflectionTestUtils
                .getField(expiringMapRefreshTokenStorage, "refreshStore");
        if (refreshStore != null) {
            refreshStore.clear();
        }
    }

    @Test
    @DisplayName("백업된 JSON 파일을 읽어 리프레시 토큰 상태를 메모리에 복원한다.")
    void restore(@TempDir Path tempDir) throws IOException {
        Path backupFile = initPath(tempDir);
        Instant now = Instant.now();
        RefreshToken token1 = new RefreshToken(1L, "token-1", Date.from(now.plus(Duration.ofMinutes(10))));
        RefreshToken token2 = new RefreshToken(2L, "token-2", Date.from(now.plus(Duration.ofMinutes(20))));
        RefreshTokens refreshTokens = new RefreshTokens(List.of(token1, token2));

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(backupFile.toFile(), refreshTokens);

        refreshTokenReloader.restore();

        assertSoftly(softly -> {
            RefreshTokens restoredTokens = expiringMapRefreshTokenStorage.getTokens();
            softly.assertThat(restoredTokens.refreshTokens()).hasSize(2);
            softly.assertThat(restoredTokens.refreshTokens())
                    .extracting(RefreshToken::value)
                    .containsExactlyInAnyOrder("token-1", "token-2");
        });
    }

    @Test
    @DisplayName("서버 종료 시 메모리의 리프레시 토큰을 JSON 파일로 백업한다. (파일/폴더 자동 생성 포함)")
    void backup(@TempDir Path tempDir) throws IOException {
        Path subDir = tempDir.resolve("backup-folder");
        Path backupFile = subDir.resolve("refresh-token-backup.json");
        ReflectionTestUtils.setField(refreshTokenReloader, "backupPath", backupFile.toString());
        Instant now = Instant.now();
        expiringMapRefreshTokenStorage.saveToken(1L, new RefreshToken(1L, "token-1", Date.from(now.plus(Duration.ofMinutes(10)))), Duration.ofMinutes(10));
        expiringMapRefreshTokenStorage.saveToken(2L, new RefreshToken(2L, "token-2", Date.from(now.plus(Duration.ofMinutes(10)))), Duration.ofMinutes(10));

        refreshTokenReloader.backup(new ContextClosedEvent(applicationContext));

        RefreshTokens backedUpTokens = objectMapper.readValue(backupFile.toFile(), RefreshTokens.class);
        assertSoftly(softly -> {
            softly.assertThat(Files.exists(backupFile)).isTrue();
            softly.assertThat(backedUpTokens.refreshTokens()).hasSize(2);
            softly.assertThat(backedUpTokens.refreshTokens())
                    .extracting(RefreshToken::identifier)
                    .containsExactlyInAnyOrder(1L, 2L);
        });
    }

    @Test
    @DisplayName("메모리에 토큰이 하나도 없으면 백업 파일을 생성하지 않는다.")
    void skipBackupWhenEmpty(@TempDir Path tempDir) {
        Path backupFile = initPath(tempDir);

        refreshTokenReloader.backup(new ContextClosedEvent(applicationContext));

        assertThat(Files.exists(backupFile)).isFalse();
    }

    private Path initPath(Path tempDir) {
        Path backupFile = tempDir.resolve("refresh_token_backup.json");
        ReflectionTestUtils.setField(refreshTokenReloader, "backupPath", backupFile.toString());
        return backupFile;
    }
}
