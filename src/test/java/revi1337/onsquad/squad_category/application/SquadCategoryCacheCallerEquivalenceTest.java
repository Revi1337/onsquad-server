package revi1337.onsquad.squad_category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadCategoryFixture.createSquadCategories;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew.application.CrewMainService;
import revi1337.onsquad.crew.application.dto.response.CrewMainResponse;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.application.SquadQueryService;
import revi1337.onsquad.squad.application.response.SquadResponse;
import revi1337.onsquad.squad.application.response.SquadWithLeaderStateResponse;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJpaRepository;
import revi1337.onsquad.squad_member.application.SquadMemberQueryService;
import revi1337.onsquad.squad_member.application.response.MyParticipantSquadResponse;
import revi1337.onsquad.squad_request.application.SquadRequestQueryService;
import revi1337.onsquad.squad_request.application.response.MySquadRequestResponse;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestJpaRepository;

@Sql({"/h2-truncate.sql", "/h2-category.sql"})
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class SquadCategoryCacheCallerEquivalenceTest extends ApplicationLayerTestSupport {
    private static final Pageable SQUAD_PAGE = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCategoryJpaRepository squadCategoryRepository;

    @Autowired
    private SquadRequestJpaRepository squadRequestRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @SpyBean
    private SquadCategoryAccessor squadCategoryAccessor;

    @Autowired
    private CrewMainService crewMainService;

    @Autowired
    private SquadQueryService squadQueryService;

    @Autowired
    private SquadMemberQueryService squadMemberQueryService;

    @Autowired
    private SquadRequestQueryService squadRequestQueryService;

    private Member revi;
    private Member andong;
    private Crew crew;
    private Squad squad1;
    private Squad squad2;
    private Squad squad3;

    @BeforeEach
    void setUp() {
        RedisCacheEvictor.flushAll(stringRedisTemplate);
        revi = memberRepository.save(createRevi());
        andong = memberRepository.save(createAndong());
        crew = createCrew(revi);
        crew.addCrewMember(CrewMemberFactory.general(crew, andong, LocalDateTime.now()));
        crewRepository.save(crew);
        squad1 = saveSquad(CategoryType.GAME, CategoryType.TENNIS);
        squad2 = saveSquad();
        squad3 = saveSquad(CategoryType.VR);
        clearPersistenceContext();
        Mockito.clearInvocations(squadCategoryAccessor);
    }

    @AfterEach
    void tearDown() {
        RedisCacheEvictor.flushAll(stringRedisTemplate);
    }

    @Test
    @DisplayName("SquadQueryService.fetchSquadsByCrewId - miss 와 hit 의 응답이 같고 저장된 카테고리와 일치한다.")
    void fetchSquadsByCrewId() {
        PageResponse<SquadResponse> miss = squadQueryService.fetchSquadsByCrewId(revi.getId(), crew.getId(), null, SQUAD_PAGE);
        Mockito.clearInvocations(squadCategoryAccessor);
        PageResponse<SquadResponse> hit = squadQueryService.fetchSquadsByCrewId(revi.getId(), crew.getId(), null, SQUAD_PAGE);

        assertSoftly(softly -> {
            softly.assertThat(hit).usingRecursiveComparison().isEqualTo(miss);
            softly.assertThat(categoriesById(hit.results(), SquadResponse::id, SquadResponse::categories)).isEqualTo(expectedCategories());
            verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
        });
    }

    @Test
    @DisplayName("SquadQueryService.fetchManageList - miss 와 hit 의 응답이 같고 저장된 카테고리와 일치한다.")
    void fetchManageList() {
        PageResponse<SquadWithLeaderStateResponse> miss = squadQueryService.fetchManageList(revi.getId(), crew.getId(), SQUAD_PAGE);
        Mockito.clearInvocations(squadCategoryAccessor);
        PageResponse<SquadWithLeaderStateResponse> hit = squadQueryService.fetchManageList(revi.getId(), crew.getId(), SQUAD_PAGE);

        assertSoftly(softly -> {
            softly.assertThat(hit).usingRecursiveComparison().isEqualTo(miss);
            softly.assertThat(categoriesById(hit.results(), SquadWithLeaderStateResponse::id, SquadWithLeaderStateResponse::categories)).isEqualTo(expectedCategories());
            verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
        });
    }

    @Test
    @DisplayName("SquadMemberQueryService.fetchMyParticipatingSquads - miss 와 hit 의 응답이 같고 저장된 카테고리와 일치한다.")
    void fetchMyParticipatingSquads() {
        List<MyParticipantSquadResponse> miss = squadMemberQueryService.fetchMyParticipatingSquads(revi.getId());
        Mockito.clearInvocations(squadCategoryAccessor);
        List<MyParticipantSquadResponse> hit = squadMemberQueryService.fetchMyParticipatingSquads(revi.getId());

        List<MyParticipantSquadResponse.MySquadParticipantResponse> hitSquads = hit.stream()
                .flatMap(response -> response.crew().squads().stream())
                .toList();
        assertSoftly(softly -> {
            softly.assertThat(hit).usingRecursiveComparison().isEqualTo(miss);
            softly.assertThat(categoriesById(hitSquads, squad -> squad.squad().id(), squad -> squad.squad().categories())).isEqualTo(expectedCategories());
            verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
        });
    }

    @Test
    @DisplayName("SquadRequestQueryService.fetchMyRequests - miss 와 hit 의 응답이 같고 저장된 카테고리와 일치한다.")
    void fetchMyRequests() {
        LocalDateTime baseTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        squadRequestRepository.saveAll(List.of(
                SquadRequest.of(squad1, andong, baseTime.plusHours(1)),
                SquadRequest.of(squad2, andong, baseTime.plusHours(2)),
                SquadRequest.of(squad3, andong, baseTime.plusHours(3))
        ));
        clearPersistenceContext();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("requestAt").descending());

        PageResponse<MySquadRequestResponse> miss = squadRequestQueryService.fetchMyRequests(andong.getId(), pageable);
        verify(squadCategoryAccessor, times(1)).fetchCategoriesBySquadIdIn(anyList());
        Mockito.clearInvocations(squadCategoryAccessor);
        PageResponse<MySquadRequestResponse> hit = squadRequestQueryService.fetchMyRequests(andong.getId(), pageable);

        assertSoftly(softly -> {
            softly.assertThat(hit).usingRecursiveComparison().isEqualTo(miss);
            softly.assertThat(categoriesById(hit.results(), request -> request.squad().id(), request -> request.squad().categories())).isEqualTo(expectedCategories());
            verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
        });
    }

    @Test
    @DisplayName("CrewMainService.fetchMain - miss 와 hit 의 스쿼드 목록이 같고 저장된 카테고리와 일치한다.")
    void fetchMain() {
        CrewMainResponse miss = crewMainService.fetchMain(revi.getId(), crew.getId(), SQUAD_PAGE);
        Mockito.clearInvocations(squadCategoryAccessor);
        CrewMainResponse hit = crewMainService.fetchMain(revi.getId(), crew.getId(), SQUAD_PAGE);

        assertSoftly(softly -> {
            softly.assertThat(hit.squads()).usingRecursiveComparison().isEqualTo(miss.squads());
            softly.assertThat(categoriesById(hit.squads(), SquadResponse::id, SquadResponse::categories)).isEqualTo(expectedCategories());
            verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
        });
    }

    @Test
    @DisplayName("스쿼드가 하나도 없는 크루의 CrewMainService.fetchMain 은 빈 squadIds 로 호출해도 빈 스쿼드 목록을 반환한다(CrewMainService 는 isNotEmpty 가드가 없다).")
    void fetchMainWithoutSquads() {
        Crew emptyCrew = crewRepository.save(createCrew(andong));
        clearPersistenceContext();

        CrewMainResponse response = crewMainService.fetchMain(andong.getId(), emptyCrew.getId(), SQUAD_PAGE);

        assertThat(response.squads()).isEmpty();
    }

    private Squad saveSquad(CategoryType... categoryTypes) {
        Squad squad = squadRepository.save(createSquad(crew, revi));
        squadCategoryRepository.saveAll(createSquadCategories(squad, categoryTypes));
        return squad;
    }

    private Map<Long, List<String>> expectedCategories() {
        return Map.of(
                squad1.getId(), List.of(CategoryType.GAME.getText(), CategoryType.TENNIS.getText()).stream().sorted().toList(),
                squad2.getId(), List.of(),
                squad3.getId(), List.of(CategoryType.VR.getText())
        );
    }

    private <T> Map<Long, List<String>> categoriesById(List<T> results, Function<T, Long> idExtractor, Function<T, List<String>> categoriesExtractor) {
        return results.stream().collect(Collectors.toMap(
                idExtractor,
                result -> categoriesExtractor.apply(result).stream().sorted().toList()
        ));
    }
}
