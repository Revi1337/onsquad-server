package revi1337.onsquad.member.infrastructure.repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.member.domain.model.VerificationStatus;
import revi1337.onsquad.member.domain.repository.VerificationCodeRepository;

@Slf4j
@Component
public class VerificationCodeRepositoryComposite implements VerificationCodeRepository {

    private static final String REPOSITORIES_CANNOT_NULL = "Repositories must not be null";
    private static final String ALL_OF_REPOSITORY_FAILED_ERROR = "All of Repository Failed. Fatal Error";
    private static final String NEXT_REPOSITORY_INVOCATION_LOG_FORMAT = "{} throws [{}]. Invoke next Repository";

    private final List<VerificationCodeRepository> mailRepositories = new ArrayList<>();

    public VerificationCodeRepositoryComposite(List<VerificationCodeRepository> mailRepositories) {
        Objects.requireNonNull(mailRepositories, REPOSITORIES_CANNOT_NULL);
        this.mailRepositories.addAll(mailRepositories);
    }

    @Override
    public long saveVerificationCode(String email, String verificationCode, Duration minutes) {
        return delegate(repository -> repository.saveVerificationCode(email, verificationCode, minutes));
    }

    @Override
    public boolean isValidVerificationCode(String email, String verificationCode) {
        return delegate(repository -> repository.isValidVerificationCode(email, verificationCode));
    }

    @Override
    public boolean markVerificationStatus(String email, VerificationStatus verificationStatus, Duration minutes) {
        return delegate(repository -> repository.markVerificationStatus(email, verificationStatus, minutes));
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus verificationStatus) {
        return delegate(repository -> repository.isMarkedVerificationStatusWith(email, verificationStatus));
    }

    private <T> T delegate(Function<VerificationCodeRepository, T> function) {
        for (VerificationCodeRepository repository : mailRepositories) {
            try {
                return function.apply(repository);
            } catch (Exception exception) {
                logNextRepositoryInvoke(repository, exception);
            }
        }
        logAllRepositoriesNonMatch();
        throw new UnsupportedOperationException(ALL_OF_REPOSITORY_FAILED_ERROR);
    }

    private void delegateVoid(Consumer<VerificationCodeRepository> consumer) {
        for (VerificationCodeRepository repository : mailRepositories) {
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

    private void logNextRepositoryInvoke(VerificationCodeRepository repository, Exception exception) {
        log.debug(NEXT_REPOSITORY_INVOCATION_LOG_FORMAT, repository.getClass().getSimpleName(), exception.getMessage());
    }

    private void logAllRepositoriesNonMatch() {
        log.error(ALL_OF_REPOSITORY_FAILED_ERROR);
    }
}
