package revi1337.onsquad.squad_category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadCategoryFixture.createSquadCategories;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.application.SquadContextHandler;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.model.SquadCreateSpec;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_category.domain.model.SimpleSquadCategory;
import revi1337.onsquad.squad_category.domain.model.SquadCategories;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJpaRepository;
import revi1337.onsquad.squad_member.application.SquadMemberCommandService;

@Sql({"/h2-truncate.sql", "/h2-category.sql"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class SquadCategoryCacheInvalidationTest extends ApplicationLayerTestSupport {
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

    @Autowired
    private SquadCategoryCacheService squadCategoryCacheService;

    @Autowired
    private SquadCommandService squadCommandService;

    @Autowired
    private SquadMemberCommandService squadMemberCommandService;

    @Autowired
    private SquadContextHandler squadContextHandler;

    @BeforeEach
    void setUp() {
        RedisCacheEvictor.flushAll(stringRedisTemplate);
    }

    @AfterEach
    void tearDown() {
        RedisCacheEvictor.flushAll(stringRedisTemplate);
    }

    @Nested
    @DisplayName("evict 가 연결된 경로 (스쿼드 삭제)")
    class evicted {
        @Test
        @DisplayName("SquadCommandService.deleteSquad - 스쿼드를 삭제하면 캐시된 카테고리가 제거된다.")
        void deleteSquad() {
            Member member = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(member));
            Squad squad = squadRepository.save(createSquad(crew, member));
            squadCategoryRepository.saveAll(createSquadCategories(squad, CategoryType.GAME, CategoryType.TENNIS));
            squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad.getId()));
            assertThat(hasCache(squad.getId())).isTrue();

            squadCommandService.deleteSquad(member.getId(), squad.getId());

            assertSoftly(softly -> {
                softly.assertThat(hasCache(squad.getId())).isFalse();
                softly.assertThat(squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad.getId())).values()).isEmpty();
            });
        }

        @Test
        @DisplayName("SquadMemberCommandService.leaveSquad - 마지막 멤버가 탈퇴해 스쿼드가 삭제되면 캐시된 카테고리가 제거된다.")
        void leaveSquadAsLastMember() {
            Member member = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(member));
            Squad squad = squadRepository.save(createSquad(crew, member));
            squadCategoryRepository.saveAll(createSquadCategories(squad, CategoryType.GAME, CategoryType.TENNIS));
            squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad.getId()));

            squadMemberCommandService.leaveSquad(member.getId(), squad.getId());

            assertThat(hasCache(squad.getId())).isFalse();
        }

        @Test
        @DisplayName("SquadContextHandler.disposeContexts - 크루 삭제/회원 탈퇴가 공통으로 경유하는 경로에서도 여러 스쿼드의 캐시가 모두 제거된다.")
        void disposeContexts() {
            Member member = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(member));
            Squad squad1 = squadRepository.save(createSquad(crew, member));
            Squad squad2 = squadRepository.save(createSquad(crew, member));
            squadCategoryRepository.saveAll(createSquadCategories(squad1, CategoryType.GAME));
            squadCategoryRepository.saveAll(createSquadCategories(squad2, CategoryType.TENNIS));
            squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad1.getId(), squad2.getId()));

            squadContextHandler.disposeContexts(List.of(squad1.getId(), squad2.getId()));

            assertSoftly(softly -> {
                softly.assertThat(hasCache(squad1.getId())).isFalse();
                softly.assertThat(hasCache(squad2.getId())).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("evict 가 연결되지 않은 경로 (스쿼드 생성)")
    class notEvicted {
        @Test
        @DisplayName("SquadCommandService.newSquad - 생성은 evict 를 하지 않지만, 새 squadId 는 캐시에 존재할 수 없으므로 첫 조회부터 저장된 카테고리가 조회된다.")
        void newSquad() {
            Member member = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(member));
            SquadCreateSpec spec = new SquadCreateSpec("title", "content", 10, "add", "add-detail", List.of(CategoryType.GAME, CategoryType.MANGACAFE), "kakao", "discord");

            Long squadId = squadCommandService.newSquad(member.getId(), crew.getId(), spec);
            SquadCategories categories = squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squadId));

            assertSoftly(softly -> {
                softly.assertThat(categories.values()).extracting(SimpleSquadCategory::categoryType)
                        .containsExactlyInAnyOrder(CategoryType.GAME, CategoryType.MANGACAFE);
                softly.assertThat(hasCache(squadId)).isTrue();
            });
        }
    }

    @Nested
    @DisplayName("[잠재 위험] 현재 코드에는 없는 '카테고리 수정' 경로를 가정한 재현")
    class hypotheticalUpdate {
        @Test
        @DisplayName("캐시된 스쿼드의 카테고리가 evict 없이 DB 에서 바뀌면, 캐시 경로는 TTL(6시간)이 끝날 때까지 이전 값을 반환한다.")
        void staleWhenCategoriesChangedWithoutEvict() {
            Member member = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(member));
            Squad squad = squadRepository.save(createSquad(crew, member));
            squadCategoryRepository.saveAll(createSquadCategories(squad, CategoryType.GAME));
            squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad.getId()));
            squadCategoryRepository.deleteAll();
            squadCategoryRepository.saveAll(createSquadCategories(squad, CategoryType.TENNIS));

            SquadCategories fromMysql = new SquadCategories(squadCategoryRepository.fetchCategoriesBySquadIdIn(List.of(squad.getId())));
            SquadCategories fromCache = squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad.getId()));

            assertSoftly(softly -> {
                softly.assertThat(fromMysql.values()).extracting(SimpleSquadCategory::categoryType).containsExactly(CategoryType.TENNIS);
                softly.assertThat(fromCache.values()).extracting(SimpleSquadCategory::categoryType).containsExactly(CategoryType.GAME);
            });

            squadCategoryCacheService.evictSquadCategories(List.of(squad.getId()));

            SquadCategories afterEvict = squadCategoryCacheService.getCategoriesBySquadIdIn(List.of(squad.getId()));
            assertThat(afterEvict.values()).extracting(SimpleSquadCategory::categoryType).containsExactly(CategoryType.TENNIS);
        }
    }

    private boolean hasCache(Long squadId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(String.format("onsquad:squad:%d:categories", squadId)));
    }
}
