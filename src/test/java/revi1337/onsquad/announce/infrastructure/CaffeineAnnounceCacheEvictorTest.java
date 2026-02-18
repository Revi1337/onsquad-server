package revi1337.onsquad.announce.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.infrastructure.storage.caffeine.CaffeineCacheManagerConfig;

class CaffeineAnnounceCacheEvictorTest extends ApplicationLayerTestSupport {

    @Autowired
    private CaffeineCacheManager caffeineCacheManager;

    @Autowired
    private CaffeineAnnounceCacheEvictor announceCacheEvictor;

    @Test
    @DisplayName("특정 크루의 단건 공지사항 캐시를 정확하게 제거한다.")
    void evictAnnounce() {
        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCE);
        cache.put("crew:1:announce:2", "data");

        announceCacheEvictor.evictAnnounce(1L, 2L);

        assertThat(cache.get("crew:1:announce:2")).isNull();
    }

    @Test
    @DisplayName("패턴 매칭을 사용하여 특정 크루의 모든 공지사항 캐시를 한꺼번에 제거한다.")
    void evictAnnouncesPattern() {
        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCE);
        cache.put("crew:1:announce:2", "data1");
        cache.put("crew:1:announce:3", "data2");
        cache.put("crew:1:announce:4", "data2");

        announceCacheEvictor.evictAnnounces(1L);

        assertSoftly(softly -> {
            softly.assertThat(cache.get("crew:1:announce:2")).isNull();
            softly.assertThat(cache.get("crew:1:announce:3")).isNull();
            softly.assertThat(cache.get("crew:1:announce:4")).isNull();
        });
    }

    @Test
    @DisplayName("여러 크루 ID를 전달받아 해당 크루들의 모든 공지사항 캐시를 벌크로 제거한다.")
    void evictAnnounces() {
        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCE);
        cache.put("crew:1:announce:2", "data1");
        cache.put("crew:1:announce:3", "data2");
        cache.put("crew:2:announce:2", "data3");
        cache.put("crew:2:announce:3", "data4");

        announceCacheEvictor.evictAnnounces(List.of(1L, 2L));

        assertSoftly(softly -> {
            softly.assertThat(cache.get("crew:1:announce:2")).isNull();
            softly.assertThat(cache.get("crew:1:announce:3")).isNull();
            softly.assertThat(cache.get("crew:2:announce:2")).isNull();
            softly.assertThat(cache.get("crew:2:announce:3")).isNull();
        });
    }

    @Test
    @DisplayName("참조 목록(References)을 기반으로 여러 공지사항 캐시를 일괄 제거한다.")
    void evictAnnouncesByReferences() {
        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCE);
        cache.put("crew:1:announce:2", "data1");
        cache.put("crew:1:announce:3", "data2");
        List<AnnounceReference> references = List.of(new AnnounceReference(1L, 2L), new AnnounceReference(1L, 3L));

        announceCacheEvictor.evictAnnouncesByReferences(references);

        assertSoftly(softly -> {
            softly.assertThat(cache.get("crew:1:announce:2")).isNull();
            softly.assertThat(cache.get("crew:1:announce:3")).isNull();
        });
    }

    @Test
    @DisplayName("공지사항 상세 내용 캐시는 유지하고 목록(List) 캐시만 선택적으로 제거한다.")
    void evictAnnounceLists() {
        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCES);
        cache.put("crew:1:announces", "datas1");
        cache.put("crew:2:announces", "datas2");
        cache.put("crew:3:announces", "datas3");

        announceCacheEvictor.evictAnnounceLists(List.of(1L, 2L, 3L));

        assertSoftly(softly -> {
            softly.assertThat(cache.get("crew:1:announces")).isNull();
            softly.assertThat(cache.get("crew:2:announces")).isNull();
            softly.assertThat(cache.get("crew:3:announces")).isNull();
        });
    }

    @TestConfiguration
    static class CacheTestConfig {

        @Bean("caffeineCacheManager")
        @Primary
        CacheManager cacheManager() {
            return new CaffeineCacheManagerConfig().caffeineCacheManager();
        }
    }
}
