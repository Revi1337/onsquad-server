package revi1337.onsquad.auth.token.infrastructure.persistence;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.token.application.RefreshTokenStorage;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;

@Slf4j
@Component
public class RefreshTokenStorageComposite implements RefreshTokenStorage {

    private static final String NAVIGATE_NEXT_HANDLER_LOG_FORMAT = "[{} 에서 예외 발생, Cause : {}] 다음 RefreshTokenManager 를 적용합니다.";

    private final List<RefreshTokenStorage> refreshTokenStorages;

    public RefreshTokenStorageComposite() {
        this.refreshTokenStorages = new ArrayList<>();
    }

    @Autowired
    public RefreshTokenStorageComposite(List<RefreshTokenStorage> refreshTokenStorages) {
        this.refreshTokenStorages = refreshTokenStorages;
    }

    @Override
    public long saveToken(Long memberId, RefreshToken refreshToken, Duration expireDuration) {
        long resultExpiredAt = 0;
        for (RefreshTokenStorage refreshTokenStorage : refreshTokenStorages) {
            try {
                resultExpiredAt = refreshTokenStorage.saveToken(memberId, refreshToken, expireDuration);
            } catch (RuntimeException exception) {
                logNavigateNextRepository(refreshTokenStorage, exception);
            }
        }

        return resultExpiredAt;
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        for (RefreshTokenStorage refreshTokenStorage : refreshTokenStorages) {
            try {
                Optional<RefreshToken> optionalToken = refreshTokenStorage.findTokenBy(memberId);
                if (optionalToken.isPresent()) {
                    return optionalToken;
                }
            } catch (RuntimeException exception) {
                logNavigateNextRepository(refreshTokenStorage, exception);
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        for (RefreshTokenStorage refreshTokenStorage : refreshTokenStorages) {
            try {
                refreshTokenStorage.deleteTokenBy(memberId);
            } catch (RuntimeException exception) {
                logNavigateNextRepository(refreshTokenStorage, exception);
            }
        }
    }

    @Override
    public void deleteAll() {
        for (RefreshTokenStorage refreshTokenStorage : refreshTokenStorages) {
            try {
                refreshTokenStorage.deleteAll();
            } catch (RuntimeException exception) {
                logNavigateNextRepository(refreshTokenStorage, exception);
            }
        }
    }

    private void logNavigateNextRepository(RefreshTokenStorage refreshTokenStorage, RuntimeException exception) {
        log.debug(NAVIGATE_NEXT_HANDLER_LOG_FORMAT, refreshTokenStorage.getClass().getSimpleName(), exception.getCause().getClass().getSimpleName());
    }
}
