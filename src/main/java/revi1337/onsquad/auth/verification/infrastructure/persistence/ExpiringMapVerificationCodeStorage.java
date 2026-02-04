package revi1337.onsquad.auth.verification.infrastructure.persistence;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;
import revi1337.onsquad.auth.verification.domain.VerificationCode;
import revi1337.onsquad.auth.verification.domain.VerificationCodes;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@Order(2)
@Component
@RequiredArgsConstructor
public class ExpiringMapVerificationCodeStorage implements VerificationCodeStorage {

    private static final int MAX_CACHE_SIZE = 2_000;

    private final ExpiringMap<String, VerificationCode> verificationStore = ExpiringMap.builder()
            .maxSize(MAX_CACHE_SIZE)
            .variableExpiration()
            .build();

    @Override
    public long saveVerificationCode(String email, String code, VerificationStatus status, Duration duration) {
        String inMemoryKey = getKey(email);
        long expectedTime = getExpectExpiredTime(duration);

        VerificationCode verification = new VerificationCode(email, code, status, expectedTime);
        verificationStore.put(inMemoryKey, verification, ExpirationPolicy.CREATED, duration.toMillis(), TimeUnit.MILLISECONDS);

        return expectedTime;
    }

    @Override
    public boolean isValidVerificationCode(String email, String code) {
        String inMemoryKey = getKey(email);
        VerificationCode verification = verificationStore.get(inMemoryKey);
        if (verification == null) {
            return false;
        }

        return Objects.equals(verification.getCode(), code);
    }

    @Override
    public synchronized boolean markVerificationStatus(String email, VerificationStatus status, Duration duration) {
        String inMemoryKey = getKey(email);
        VerificationCode verification = verificationStore.get(inMemoryKey);
        if (verification != null) {
            verificationStore.remove(inMemoryKey);
            long expectedTime = getExpectExpiredTime(duration);
            VerificationCode updated = new VerificationCode(email, verification.getCode(), status, expectedTime);
            verificationStore.put(inMemoryKey, updated, duration.toMillis(), TimeUnit.MILLISECONDS);
            return true;
        }

        return false;
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus status) {
        String inMemoryKey = getKey(email);
        VerificationCode verification = verificationStore.get(inMemoryKey);

        return verification.getStatus() == status;
    }

    public synchronized VerificationCodes getVerificationCodes() {
        List<VerificationCode> verificationCodes = verificationStore.values()
                .stream()
                .toList();

        return new VerificationCodes(verificationCodes);
    }

    private String getKey(String email) {
        return String.format(CacheFormat.COMPLEX, CacheConst.VERIFICATION_CODE, email);
    }

    private long getExpectExpiredTime(Duration duration) {
        return Instant.now()
                .plus(duration)
                .toEpochMilli();
    }
}
