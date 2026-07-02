package revi1337.onsquad.auth.verification.infrastructure.persistence;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;
import revi1337.onsquad.auth.verification.domain.VerificationCode;
import revi1337.onsquad.auth.verification.domain.VerificationCodes;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.infrastructure.storage.caffeine.TimedEntry;

@Order(2)
@Component
public class CaffeineVerificationCodeStorage implements VerificationCodeStorage {

    private static final int MAX_CACHE_SIZE = 2_000;

    private static final Expiry<String, TimedEntry<VerificationCode>> EXPIRY = new Expiry<>() {
        @Override
        public long expireAfterCreate(String key, TimedEntry<VerificationCode> entry, long currentTime) {
            return entry.ttlNanos();
        }

        @Override
        public long expireAfterUpdate(String key, TimedEntry<VerificationCode> entry, long currentTime, long currentDuration) {
            return entry.ttlNanos();
        }

        @Override
        public long expireAfterRead(String key, TimedEntry<VerificationCode> entry, long currentTime, long currentDuration) {
            return currentDuration;
        }
    };

    private final Cache<String, TimedEntry<VerificationCode>> verificationStore =
            Caffeine.newBuilder()
                    .maximumSize(MAX_CACHE_SIZE)
                    .expireAfter(EXPIRY)
                    .build();

    @Override
    public long saveVerificationCode(String email, String code, VerificationStatus status, Duration expireDuration) {
        String inMemoryKey = getKey(email);
        long expectedTime = getExpectExpiredTime(expireDuration);
        VerificationCode verification = new VerificationCode(email, code, status, expectedTime);
        verificationStore.put(inMemoryKey, new TimedEntry<>(verification, expireDuration.toNanos()));

        return expectedTime;
    }

    @Override
    public boolean isValidVerificationCode(String email, String code) {
        String inMemoryKey = getKey(email);
        TimedEntry<VerificationCode> entry = verificationStore.getIfPresent(inMemoryKey);
        if (entry == null) {
            return false;
        }

        return Objects.equals(entry.value().getCode(), code);
    }

    @Override
    public synchronized boolean markVerificationStatus(String email, VerificationStatus status, Duration expireDuration) {
        String inMemoryKey = getKey(email);
        TimedEntry<VerificationCode> entry = verificationStore.getIfPresent(inMemoryKey);
        if (entry != null) {
            verificationStore.invalidate(inMemoryKey);
            long expectedTime = getExpectExpiredTime(expireDuration);
            VerificationCode updated = new VerificationCode(email, entry.value().getCode(), status, expectedTime);
            verificationStore.put(inMemoryKey, new TimedEntry<>(updated, expireDuration.toNanos()));
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean markVerificationStatusAsSuccess(String email, String authCode, Duration expireDuration) {
        String inMemoryKey = getKey(email);
        TimedEntry<VerificationCode> entry = verificationStore.getIfPresent(inMemoryKey);
        if (entry != null && Objects.equals(entry.value().getCode(), authCode) && entry.value().getStatus() != VerificationStatus.SUCCESS) {
            return markVerificationStatus(email, VerificationStatus.SUCCESS, expireDuration);
        }

        return false;
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus status) {
        String inMemoryKey = getKey(email);
        TimedEntry<VerificationCode> entry = verificationStore.getIfPresent(inMemoryKey);

        return entry.value().getStatus() == status;
    }

    public synchronized VerificationCodes getVerificationCodes() {
        List<VerificationCode> verificationCodes = verificationStore.asMap().values().stream()
                .map(TimedEntry::value)
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
