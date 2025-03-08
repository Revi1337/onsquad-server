package revi1337.onsquad.announce.application.listener;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;

// TODO RedisCacheAspect 와 겹치는 로직을 리팩토링 해야한다.
@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceFixedEventListener {

    private final AnnounceRepository announceRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @TransactionalEventListener
    public void handleAnnounceFixedEvent(AnnounceFixedEvent fixedEvent) {
        log.debug("[{}] Renew fixed announces caches in crew_id = {}", fixedEvent.getEventName(), fixedEvent.crewId());
        List<AnnounceInfoDomainDto> announceInfos = announceRepository.findLimitedAnnouncesByCrewId(
                fixedEvent.crewId());
        String redisKey = String.format("onsquad:limit-announces:crew:%d", fixedEvent.crewId());
        redisTemplate.opsForValue().set(redisKey, announceInfos, Duration.ofHours(1));
    }
}
