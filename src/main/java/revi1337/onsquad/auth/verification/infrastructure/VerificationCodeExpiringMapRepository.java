package revi1337.onsquad.auth.verification.infrastructure;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.auth.verification.domain.VerificationCodeRepository;
import revi1337.onsquad.auth.verification.domain.VerificationSnapshot;
import revi1337.onsquad.auth.verification.domain.VerificationSnapshots;
import revi1337.onsquad.auth.verification.domain.VerificationState;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@RequiredArgsConstructor
@Order(2)
@Repository
public class VerificationCodeExpiringMapRepository implements VerificationCodeRepository {

    private static final int MAX_CACHE_SIZE = 10_000;
    private static final Map<String, VerificationState> VERIFICATION_TRACKER = new ConcurrentHashMap<>();
    private static final ExpiringMap<String, String> VERIFICATION_STORE = ExpiringMap.builder()
            .maxSize(MAX_CACHE_SIZE)
            .expirationListener((key, value) -> VERIFICATION_TRACKER.remove(key))
            .variableExpiration()
            .build();

    @Override
    public long saveVerificationCode(String email, String verificationCode, Duration duration) {
        String inMemoryKey = getKey(email);
        long expectedTime = getExpiredTime(duration);

        VERIFICATION_STORE.put(inMemoryKey, verificationCode, ExpirationPolicy.CREATED, duration.toMillis(), TimeUnit.MILLISECONDS);
        VERIFICATION_TRACKER.put(inMemoryKey, new VerificationState(verificationCode, email, expectedTime));

        return expectedTime;
    }

    @Override
    public boolean isValidVerificationCode(String email, String verificationCode) {
        String inMemoryKey = getKey(email);
        String extractedCode = VERIFICATION_STORE.get(inMemoryKey);

        return Objects.equals(extractedCode, verificationCode);
    }

    @Override
    public boolean markVerificationStatus(String email, VerificationStatus verificationStatus, Duration duration) {
        String inMemoryKey = getKey(email);
        if (VERIFICATION_STORE.containsKey(inMemoryKey)) {
            long expectedTime = getExpiredTime(duration);
            VerificationState state = new VerificationState(verificationStatus.name(), email, expectedTime);
            VERIFICATION_STORE.put(inMemoryKey, verificationStatus.name(), duration.toMillis(), TimeUnit.MILLISECONDS);
            VERIFICATION_TRACKER.put(inMemoryKey, state);
            return true;
        }

        return false;
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus verificationStatus) {
        String redisKey = getKey(email);

        return Objects.equals(VERIFICATION_STORE.get(redisKey), verificationStatus.name());
    }

    public VerificationSnapshots collectAvailableSnapshots() {
        return new VerificationSnapshots(VERIFICATION_TRACKER.keySet().stream()
                .map(key -> new VerificationSnapshot(key, VERIFICATION_TRACKER.get(key)))
                .toList());
    }

    private String getKey(String email) {
        return String.format(CacheFormat.COMPLEX, CacheConst.VERIFICATION_CODE, email);
    }

    private long getExpiredTime(Duration duration) {
        return Instant.now()
                .plusMillis(duration.toMillis())
                .atZone(TimeZone.getDefault().toZoneId())
                .toInstant()
                .toEpochMilli();
    }
}
