package revi1337.onsquad.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FailoverHandler {

    private final RefreshTokenFailoverService refreshTokenFailoverService;

    @GetMapping("/failover")
    public void failover() {
        log.debug("Starting to Failover Process");
        refreshTokenFailoverService.migrateTokensToRedis();
        log.debug("Failover Process Ended");
    }
}
