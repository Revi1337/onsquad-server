package revi1337.onsquad.squad_category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadCategoryFixture.createSquadCategories;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.common.util.ObjectMapperUtils;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.SquadLinkable;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.model.SquadLinkableGroup;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_category.domain.model.SimpleSquadCategory;
import revi1337.onsquad.squad_category.domain.model.SquadCategories;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJpaRepository;

@Sql({"/h2-truncate.sql", "/h2-category.sql"})
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class SquadCategoryCacheEquivalenceTest extends ApplicationLayerTestSupport {
    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCategoryJpaRepository squadCategoryRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @SpyBean
    private SquadCategoryAccessor squadCategoryAccessor;

    @Autowired
    private SquadCategoryCacheService squadCategoryCacheService;

    private Crew crew;
    private Member member;

    @BeforeEach
    void setUp() {
        RedisCacheEvictor.flushAll(stringRedisTemplate);
        member = memberRepository.save(createRevi());
        crew = crewRepository.save(createCrew(member));
    }

    @AfterEach
    void tearDown() {
        RedisCacheEvictor.flushAll(stringRedisTemplate);
    }

    @Nested
    @DisplayName("캐시 miss 경로 vs MySQL 경로")
    class cacheMiss {
        @Test
        @DisplayName("캐시가 비어있으면 MySQL 경로와 동일한 순서/내용의 결과를 반환한다.")
        void sameAsMysql() {
            Squad squad1 = saveSquad(CategoryType.ACTIVITY, CategoryType.PERFORMANCE, CategoryType.GAME);
            Squad squad2 = saveSquad(CategoryType.BADMINTON, CategoryType.ESCAPEROOM);
            Squad squad3 = saveSquad(CategoryType.VR);
            List<Long> squadIds = List.of(squad1.getId(), squad2.getId(), squad3.getId());
            SquadCategories fromMysql = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadIds);

            SquadCategories fromCache = squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds);

            assertSoftly(softly -> {
                softly.assertThat(fromCache.values()).containsExactlyElementsOf(fromMysql.values());
                softly.assertThat(fromCache.groupBySquadId()).isEqualTo(fromMysql.groupBySquadId());
                softly.assertThat(link(squadIds, fromCache)).isEqualTo(link(squadIds, fromMysql));
            });
        }

        @Test
        @DisplayName("스쿼드 내부의 카테고리 순서도 MySQL 경로와 동일하다.")
        void sameCategoryOrderWithinSquad() {
            Squad squad = saveSquad(CategoryType.TENNIS, CategoryType.GAME, CategoryType.SNOWFESTIVAL, CategoryType.BADMINTON);
            List<Long> squadIds = List.of(squad.getId());
            List<CategoryType> mysqlOrder = link(squadIds, squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadIds)).get(squad.getId());

            List<CategoryType> missOrder = link(squadIds, squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds)).get(squad.getId());
            List<CategoryType> hitOrder = link(squadIds, squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds)).get(squad.getId());

            assertSoftly(softly -> {
                softly.assertThat(missOrder).containsExactlyElementsOf(mysqlOrder);
                softly.assertThat(hitOrder).containsExactlyElementsOf(mysqlOrder);
            });
        }
    }

    @Nested
    @DisplayName("캐시 hit 경로 vs MySQL 경로")
    class cacheHit {
        @Test
        @DisplayName("전부 캐시에 있으면 DB 조회 없이 MySQL 경로와 동일한 결과를 반환한다.")
        void sameAsMysql() {
            Squad squad1 = saveSquad(CategoryType.ACTIVITY, CategoryType.PERFORMANCE, CategoryType.GAME);
            Squad squad2 = saveSquad(CategoryType.BADMINTON, CategoryType.ESCAPEROOM);
            Squad squad3 = saveSquad(CategoryType.VR);
            List<Long> squadIds = List.of(squad1.getId(), squad2.getId(), squad3.getId());
            SquadCategories fromMysql = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadIds);
            squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds);
            Mockito.clearInvocations(squadCategoryAccessor);

            SquadCategories fromCache = squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds);

            assertSoftly(softly -> {
                verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
                softly.assertThat(fromCache.values()).containsExactlyInAnyOrderElementsOf(fromMysql.values());
                softly.assertThat(fromCache.groupBySquadId()).isEqualTo(fromMysql.groupBySquadId());
                softly.assertThat(link(squadIds, fromCache)).isEqualTo(link(squadIds, fromMysql));
            });
        }

        @Test
        @DisplayName("요청 ID 순서가 오름차순이 아니면 values() 의 전체 순서는 요청 ID 순서를 따르지만, 호출처가 소비하는 스쿼드별 결과는 MySQL 경로와 같다.")
        void globalOrderFollowsRequestOrderButLinkedResultIsSame() {
            Squad squad1 = saveSquad(CategoryType.ACTIVITY, CategoryType.PERFORMANCE);
            Squad squad2 = saveSquad(CategoryType.BADMINTON, CategoryType.ESCAPEROOM);
            Squad squad3 = saveSquad(CategoryType.VR, CategoryType.GAME);
            List<Long> requestOrder = List.of(squad3.getId(), squad1.getId(), squad2.getId());
            SquadCategories fromMysql = squadCategoryAccessor.fetchCategoriesBySquadIdIn(requestOrder);
            squadCategoryCacheService.getCategoriesBySquadIdIn(requestOrder);

            SquadCategories fromCache = squadCategoryCacheService.getCategoriesBySquadIdIn(requestOrder);

            assertSoftly(softly -> {
                softly.assertThat(squadIdsInEncounterOrder(fromCache)).containsExactlyElementsOf(requestOrder);
                softly.assertThat(fromCache.values()).containsExactlyInAnyOrderElementsOf(fromMysql.values());
                softly.assertThat(link(requestOrder, fromCache)).isEqualTo(link(requestOrder, fromMysql));
            });
        }
    }

    @Nested
    @DisplayName("hit + miss 혼합")
    class partialHit {
        @Test
        @DisplayName("캐시 hit 항목이 앞에, miss 항목이 뒤에 붙지만 호출처가 소비하는 스쿼드별 결과는 MySQL 경로와 같다.")
        void hitFirstThenMissButLinkedResultIsSame() {
            Squad squad1 = saveSquad(CategoryType.ACTIVITY, CategoryType.PERFORMANCE);
            Squad squad2 = saveSquad(CategoryType.BADMINTON, CategoryType.ESCAPEROOM);
            Squad squad3 = saveSquad(CategoryType.VR, CategoryType.GAME);
            List<Long> squadIds = List.of(squad1.getId(), squad2.getId(), squad3.getId());
            SquadCategories fromMysql = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadIds);
            squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad2.getId()));
            Mockito.clearInvocations(squadCategoryAccessor);
            ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);

            SquadCategories fromCache = squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds);

            assertSoftly(softly -> {
                verify(squadCategoryAccessor, times(1)).fetchCategoriesBySquadIdIn(captor.capture());
                softly.assertThat(captor.getValue()).containsExactly(squad1.getId(), squad3.getId());
                softly.assertThat(squadIdsInEncounterOrder(fromCache)).containsExactly(squad2.getId(), squad1.getId(), squad3.getId());
                softly.assertThat(fromCache.values()).containsExactlyInAnyOrderElementsOf(fromMysql.values());
                softly.assertThat(link(squadIds, fromCache)).isEqualTo(link(squadIds, fromMysql));
            });
        }
    }

    @Nested
    @DisplayName("카테고리가 없는 스쿼드 / 빈 입력")
    class emptyCases {
        @Test
        @DisplayName("카테고리가 없는 스쿼드는 MySQL 경로처럼 결과에 나타나지 않고, 빈 값이 캐싱되어 다음 조회에서는 DB 를 조회하지 않는다.")
        void squadWithoutCategories() {
            Squad withCategories = saveSquad(CategoryType.GAME);
            Squad withoutCategories = saveSquad();
            List<Long> squadIds = List.of(withCategories.getId(), withoutCategories.getId());
            SquadCategories fromMysql = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadIds);

            SquadCategories miss = squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds);
            String cachedEmptyJson = stringRedisTemplate.opsForValue().get(cacheKey(withoutCategories.getId()));
            Mockito.clearInvocations(squadCategoryAccessor);
            SquadCategories hit = squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds);

            assertSoftly(softly -> {
                softly.assertThat(miss.values()).containsExactlyElementsOf(fromMysql.values());
                softly.assertThat(hit.values()).containsExactlyElementsOf(fromMysql.values());
                softly.assertThat(link(squadIds, hit)).isEqualTo(link(squadIds, fromMysql));
                softly.assertThat(link(squadIds, hit).get(withoutCategories.getId())).isEmpty();
                softly.assertThat(cachedEmptyJson).isNotNull();
                verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
            });
        }

        @Test
        @DisplayName("squadIds 가 비어있으면 캐시 경로는 Redis/DB 를 거치지 않고 빈 결과를 반환하며, MySQL 경로도 빈 결과를 반환한다.")
        void emptySquadIds() {
            SquadCategories fromMysql = squadCategoryAccessor.fetchCategoriesBySquadIdIn(List.of());
            Mockito.clearInvocations(squadCategoryAccessor);

            SquadCategories fromCache = squadCategoryCacheService.getCategoriesBySquadIdIn(List.of());

            assertSoftly(softly -> {
                softly.assertThat(fromMysql.values()).isEmpty();
                softly.assertThat(fromCache.values()).isEmpty();
                verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
            });
        }
    }

    @Nested
    @DisplayName("JSON 직렬화/역직렬화 왕복")
    class jsonRoundTrip {
        @Test
        @DisplayName("Redis 에 저장된 JSON 을 역직렬화하면 MySQL 에서 조회한 값과 동일하다.")
        void storedJsonEqualsMysqlValues() {
            Squad squad = saveSquad(CategoryType.TENNIS, CategoryType.GAME, CategoryType.SNOWFESTIVAL);
            List<Long> squadIds = List.of(squad.getId());
            SquadCategories fromMysql = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadIds);
            squadCategoryCacheService.getCategoriesBySquadIdIn(squadIds);

            String storedJson = stringRedisTemplate.opsForValue().get(cacheKey(squad.getId()));
            SquadCategories deserialized = ObjectMapperUtils.deserialize(serviceObjectMapper(), storedJson, SquadCategories.class);

            assertSoftly(softly -> {
                softly.assertThat(storedJson).isNotNull();
                softly.assertThat(deserialized.values()).containsExactlyElementsOf(fromMysql.values());
                softly.assertThat(deserialized.groupBySquadId()).isEqualTo(fromMysql.groupBySquadId());
            });
        }

        @Test
        @DisplayName("모든 CategoryType 이 서비스가 사용하는 ObjectMapper 로 직렬화/역직렬화 왕복 후에도 동일하다.")
        void allCategoryTypesSurviveRoundTrip() {
            ObjectMapper objectMapper = serviceObjectMapper();
            List<SimpleSquadCategory> source = Arrays.stream(CategoryType.values())
                    .map(type -> new SimpleSquadCategory(7L, type))
                    .toList();

            String json = ObjectMapperUtils.serializeToString(objectMapper, new SquadCategories(source));
            SquadCategories restored = ObjectMapperUtils.deserialize(objectMapper, json, SquadCategories.class);

            assertThat(restored.values()).containsExactlyElementsOf(source);
        }

        @Test
        @DisplayName("빈 SquadCategories 도 왕복 후 빈 값으로 복원된다.")
        void emptyCategoriesSurviveRoundTrip() {
            ObjectMapper objectMapper = serviceObjectMapper();

            String json = ObjectMapperUtils.serializeToString(objectMapper, new SquadCategories());
            SquadCategories restored = ObjectMapperUtils.deserialize(objectMapper, json, SquadCategories.class);

            assertThat(restored.values()).isEmpty();
        }
    }

    @Nested
    @DisplayName("중복 squadId 입력 (현재 호출처에서는 도달 불가능한 차이)")
    class duplicatedSquadIds {
        @Test
        @DisplayName("같은 squadId 가 중복되면 캐시 hit 경로는 중복된 만큼 카테고리를 반복해서 반환하지만 MySQL 경로(IN 절)는 한 번만 반환한다.")
        void hitPathRepeatsCategoriesForDuplicatedIds() {
            Squad squad = saveSquad(CategoryType.GAME, CategoryType.TENNIS);
            List<Long> duplicated = List.of(squad.getId(), squad.getId());
            SquadCategories fromMysql = squadCategoryAccessor.fetchCategoriesBySquadIdIn(duplicated);
            squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad.getId()));

            SquadCategories fromCacheHit = squadCategoryCacheService.getCategoriesBySquadIdIn(duplicated);

            assertSoftly(softly -> {
                softly.assertThat(fromMysql.values()).hasSize(2);
                softly.assertThat(fromCacheHit.values()).hasSize(4);
            });
        }
    }

    private Squad saveSquad(CategoryType... categoryTypes) {
        Squad squad = squadRepository.save(createSquad(crew, member));
        squadCategoryRepository.saveAll(createSquadCategories(squad, categoryTypes));
        return squad;
    }

    private ObjectMapper serviceObjectMapper() {
        return (ObjectMapper) ReflectionTestUtils.getField(squadCategoryCacheService, "defaultObjectMapper");
    }

    private static String cacheKey(Long squadId) {
        return String.format("onsquad:squad:%d:categories", squadId);
    }

    private static List<Long> squadIdsInEncounterOrder(SquadCategories categories) {
        return categories.values().stream()
                .map(SimpleSquadCategory::squadId)
                .distinct()
                .toList();
    }

    private static Map<Long, List<CategoryType>> link(List<Long> squadIds, SquadCategories categories) {
        List<LinkableStub> stubs = squadIds.stream().map(LinkableStub::new).toList();
        new SquadLinkableGroup<>(stubs).linkCategories(categories);
        return stubs.stream().collect(java.util.stream.Collectors.toMap(stub -> stub.id, stub -> stub.categories));
    }

    private static class LinkableStub implements SquadLinkable {
        private final Long id;
        private final List<CategoryType> categories = new ArrayList<>();

        private LinkableStub(Long id) {
            this.id = id;
        }

        @Override
        public Long getSquadId() {
            return id;
        }

        @Override
        public void addCategories(List<CategoryType> categories) {
            this.categories.addAll(categories);
        }
    }
}
