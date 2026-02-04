package revi1337.onsquad.auth.verification.infrastructure.persistence;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

@Slf4j
@Component
public class VerificationCodeStorageComposite implements VerificationCodeStorage {

    private static final String REPOSITORIES_CANNOT_NULL = "Repositories must not be null";
    private static final String ALL_OF_REPOSITORY_FAILED_ERROR = "All of Repository Failed. Fatal Error";
    private static final String NEXT_REPOSITORY_INVOCATION_LOG_FORMAT = "{} throws [{}]. Invoke next Repository";

    private final List<VerificationCodeStorage> mailRepositories = new ArrayList<>();

    public VerificationCodeStorageComposite(List<VerificationCodeStorage> mailRepositories) {
        Objects.requireNonNull(mailRepositories, REPOSITORIES_CANNOT_NULL);
        this.mailRepositories.addAll(mailRepositories);
    }

    @Override
    public long saveVerificationCode(String email, String code, VerificationStatus status, Duration minutes) {
        return delegate(repository -> repository.saveVerificationCode(email, code, status, minutes));
    }

    @Override
    public boolean isValidVerificationCode(String email, String code) {
        return delegate(repository -> repository.isValidVerificationCode(email, code));
    }

    @Override
    public boolean markVerificationStatus(String email, VerificationStatus status, Duration minutes) {
        return delegate(repository -> repository.markVerificationStatus(email, status, minutes));
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus status) {
        return delegate(repository -> repository.isMarkedVerificationStatusWith(email, status));
    }

    private <T> T delegate(Function<VerificationCodeStorage, T> function) {
        for (VerificationCodeStorage repository : mailRepositories) {
            try {
                return function.apply(repository);
            } catch (Exception exception) {
                logNextRepositoryInvoke(repository, exception);
            }
        }
        logAllRepositoriesNonMatch();
        throw new UnsupportedOperationException(ALL_OF_REPOSITORY_FAILED_ERROR);
    }

    private void delegateVoid(Consumer<VerificationCodeStorage> consumer) {
        for (VerificationCodeStorage repository : mailRepositories) {
            try {
                consumer.accept(repository);
                return;
            } catch (Exception exception) {
                logNextRepositoryInvoke(repository, exception);
            }
        }
        logAllRepositoriesNonMatch();
        throw new UnsupportedOperationException(ALL_OF_REPOSITORY_FAILED_ERROR);
    }

    private void logNextRepositoryInvoke(VerificationCodeStorage repository, Exception exception) {
        log.debug(NEXT_REPOSITORY_INVOCATION_LOG_FORMAT, repository.getClass().getSimpleName(), exception.getMessage());
    }

    private void logAllRepositoriesNonMatch() {
        log.error(ALL_OF_REPOSITORY_FAILED_ERROR);
    }
}
