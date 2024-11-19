package revi1337.onsquad.announce.application.listener;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;

// TODO RedisCacheAspect 와 겹치는 로직을 리팩토링 해야한다.
@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceCreateEventListener {

    private final AnnounceRepository announceRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @TransactionalEventListener
    public void handleAnnounceCreateEvent(AnnounceCreateEvent createEvent) {
        log.debug("[{}] Renew new announces caches in crew_id = {}", createEvent.getEventName(), createEvent.crewId());
        List<AnnounceInfoDomainDto> announceInfos = announceRepository.findLimitedAnnouncesByCrewId(
                createEvent.crewId());
        String redisKey = String.format("onsquad:crew:%d:limit-announces", createEvent.crewId());
        redisTemplate.opsForValue().set(redisKey, announceInfos, Duration.ofHours(1));

    }
}
