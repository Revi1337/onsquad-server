package revi1337.onsquad.inrastructure.mail.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepository;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepositoryCandidates;

@Configuration
public class MailRepositoryConfig {

    @Bean("repositoryChain")
    public VerificationCodeRepository verificationCodeRepositoryCandidates(
            VerificationCodeRepository redisCodeRepository,
            VerificationCodeRepository expiringMapCodeRepository
    ) {
        return new VerificationCodeRepositoryCandidates(List.of(redisCodeRepository, expiringMapCodeRepository));
    }
}
