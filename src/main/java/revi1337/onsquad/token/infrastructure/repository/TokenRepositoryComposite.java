package revi1337.onsquad.token.infrastructure.repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.domain.repository.TokenRepository;

@Deprecated
@Slf4j
@Component
public class TokenRepositoryComposite implements TokenRepository {

    private static final String NAVIGATE_NEXT_HANDLER_LOG_FORMAT = "[{} 에서 예외 발생, Cause : {}] 다음 TokenRepository 를 적용합니다.";

    private final List<TokenRepository> tokenRepositories;

    public TokenRepositoryComposite() {
        this.tokenRepositories = new ArrayList<>();
    }

    @Autowired
    public TokenRepositoryComposite(List<TokenRepository> tokenRepositories) {
        this.tokenRepositories = tokenRepositories;
    }

    @Override
    public void save(RefreshToken refreshToken, Long memberId, Duration expired) {
        for (TokenRepository tokenRepository : tokenRepositories) {
            try {
                tokenRepository.save(refreshToken, memberId, expired);
            } catch (RuntimeException exception) {
                logNavigateNextRepository(tokenRepository, exception);
            }
        }
    }

    @Override
    public Optional<RefreshToken> findBy(Long memberId) {
        for (TokenRepository tokenRepository : tokenRepositories) {
            try {
                Optional<RefreshToken> optionalToken = tokenRepository.findBy(memberId);
                if (optionalToken.isPresent()) {
                    return optionalToken;
                }
            } catch (RuntimeException exception) {
                logNavigateNextRepository(tokenRepository, exception);
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteBy(Long memberId) {
        for (TokenRepository tokenRepository : tokenRepositories) {
            try {
                tokenRepository.deleteBy(memberId);
            } catch (RuntimeException exception) {
                logNavigateNextRepository(tokenRepository, exception);
            }
        }
    }

    @Override
    public void deleteAll() {
        for (TokenRepository tokenRepository : tokenRepositories) {
            try {
                tokenRepository.deleteAll();
            } catch (RuntimeException exception) {
                logNavigateNextRepository(tokenRepository, exception);
            }
        }
    }

    private void logNavigateNextRepository(TokenRepository tokenRepository, RuntimeException exception) {
        log.debug(NAVIGATE_NEXT_HANDLER_LOG_FORMAT, tokenRepository.getClass().getSimpleName(), exception.getCause().getClass().getSimpleName());
    }
}
