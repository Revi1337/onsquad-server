package revi1337.onsquad.inrastructure.mail.repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;

@Slf4j
public class VerificationCodeRepositoryCandidates implements VerificationCodeRepository {

    private final List<VerificationCodeRepository> mailRepositories = new ArrayList<>();

    public VerificationCodeRepositoryCandidates(List<VerificationCodeRepository> mailRepositories) {
        this.mailRepositories.addAll(mailRepositories);
    }

    @Override
    public void saveVerificationCode(String email, String verificationCode, Duration minutes) {
        delegateVoid(repository -> repository.saveVerificationCode(email, verificationCode, minutes));
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
                printNextRepositoryNavigateLog(repository, exception);
            }
        }
        printNoneMatchRepositoryLog();
        throw new UnsupportedOperationException("All of Repository Failed. Fatal Error");
    }

    private void delegateVoid(Consumer<VerificationCodeRepository> consumer) {
        for (VerificationCodeRepository repository : mailRepositories) {
            try {
                consumer.accept(repository);
                return;
            } catch (Exception exception) {
                printNextRepositoryNavigateLog(repository, exception);
            }
        }
        printNoneMatchRepositoryLog();
        throw new UnsupportedOperationException("All of Repository Failed. Fatal Error");
    }

    private void printNextRepositoryNavigateLog(VerificationCodeRepository repository, Exception exception) {
        log.info("{} throws [{}]. Invoke next Repository", repository.getClass().getSimpleName(),
                exception.getMessage());
    }

    private void printNoneMatchRepositoryLog() {
        log.error("All of Repository Failed. Fatal Error");
    }
}
