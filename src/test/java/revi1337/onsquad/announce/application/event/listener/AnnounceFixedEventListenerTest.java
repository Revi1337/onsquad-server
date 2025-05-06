package revi1337.onsquad.announce.application.event.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.transaction.TestTransaction;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.AnnounceQueryDslRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.constant.CacheConst;

class AnnounceFixedEventListenerTest extends ApplicationLayerTestSupport {

    @SpyBean
    private AnnounceQueryDslRepository announceRepository;

    @MockBean
    private CacheManager cacheManager;

    @MockBean
    private Cache cache;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @SpyBean
    private AnnounceFixedEventListener eventListener;

    @Test
    @DisplayName("Announce 상단 고정 TransactionEventListener 가 동작하는지 검증한다.")
    void success1() {
        // given
        Long DUMMY_CREW_ID = 1L;
        Long DUMMY_ANNOUNCE_ID = 2L;
        AnnounceFixedEvent EVENT = new AnnounceFixedEvent(DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID);

        // when
        eventPublisher.publishEvent(EVENT);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        // then
        verify(eventListener).handleAnnounceFixedEvent(EVENT);
    }

    @Test
    @DisplayName("Announce 상단 고정 TransactionEventListener 내부 동작을 검증한다.")
    void success2() {
        // given
        Long DUMMY_CREW_ID = 1L;
        Long DUMMY_ANNOUNCE_ID = 2L;
        AnnounceFixedEvent EVENT = new AnnounceFixedEvent(DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID);
        AnnounceDomainDto ANNOUNCE = mock(AnnounceDomainDto.class);
        when(announceRepository.fetchByCrewIdAndId(DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID))
                .thenReturn(Optional.of(ANNOUNCE));
        when(announceRepository.fetchAllInDefaultByCrewId(DUMMY_CREW_ID))
                .thenReturn(List.of(ANNOUNCE));
        when(cacheManager.getCache(CacheConst.CREW_ANNOUNCE)).thenReturn(cache);
        when(cacheManager.getCache(CacheConst.CREW_ANNOUNCES)).thenReturn(cache);

        // when
        eventPublisher.publishEvent(EVENT);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        // then
        verify(announceRepository).fetchByCrewIdAndId(DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID);
        verify(cacheManager).getCache(CacheConst.CREW_ANNOUNCE);
        verify(cache).evict(String.format("crew:%d:announce:%d", DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID));
        verify(cache).put(String.format("crew:%d:announce:%d", DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID), ANNOUNCE);

        verify(announceRepository).fetchAllInDefaultByCrewId(DUMMY_CREW_ID);
        verify(cacheManager).getCache(CacheConst.CREW_ANNOUNCES);
        verify(cache).evict(String.format("crew:%d", DUMMY_CREW_ID));
        verify(cache).put(eq(String.format("crew:%d", DUMMY_CREW_ID)), any());
    }

}