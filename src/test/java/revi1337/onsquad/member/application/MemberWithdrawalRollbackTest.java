package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadCategoryFixture.createSquadCategories;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceJpaRepository;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagJdbcRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagJpaRepository;
import revi1337.onsquad.crew.application.CrewContextDisposer;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestJpaRepository;
import revi1337.onsquad.hashtag.domain.HashtagType;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestJpaRepository;

class MemberWithdrawalRollbackTest extends ApplicationLayerTestSupport {

    private static final String INJECTED_FAILURE = "injected failure";

    @Autowired
    private MemberContextHandler memberContextHandler;

    @Autowired
    private MemberContextDisposer memberContextDisposer;

    @Autowired
    private CrewContextDisposer crewContextDisposer;

    private final List<Runnable> restorations = new ArrayList<>();

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestJpaRepository;

    @Autowired
    private CrewHashtagJdbcRepository crewHashtagJdbcRepository;

    @Autowired
    private CrewHashtagJpaRepository crewHashtagJpaRepository;

    @Autowired
    private AnnounceJpaRepository announceJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Autowired
    private SquadRequestJpaRepository squadRequestJpaRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentJpaRepository;

    @Autowired
    private SquadCategoryJpaRepository squadCategoryJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberCommandService memberCommandService;

    @BeforeEach
    void seedReferenceData() {
        executeScripts("h2-truncate.sql", "h2-category.sql", "h2-hashtag.sql");
    }

    @AfterEach
    void restoreAndCleanUp() {
        restorations.forEach(Runnable::run);
        restorations.clear();
        executeScripts("h2-truncate.sql");
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("마지막 단계인 회원 삭제에서 실패하면 앞 단계에서 삭제하고 갱신한 크루, 스쿼드, 댓글, 공지 등이 모두 원래대로 돌아온다")
    void rollsBackEverything_whenMemberDeletionFails() {
        Scenario scenario = createScenario();
        Map<String, Long> before = snapshot();
        AtomicReference<Map<String, Long>> observedInTransaction = new AtomicReference<>();
        doAnswer(invocation -> {
            observedInTransaction.set(snapshot());
            throw new DataAccessResourceFailureException(INJECTED_FAILURE);
        }).when(replaceDependency(memberContextHandler, "memberRepository", MemberRepository.class)).deleteById(anyLong());

        assertThatThrownBy(() -> memberCommandService.deleteMember(scenario.me().getId()))
                .isInstanceOf(DataAccessResourceFailureException.class)
                .hasMessage(INJECTED_FAILURE);

        assertSoftly(softly -> {
            softly.assertThat(observedInTransaction.get().get("crew")).as("실패 시점에 소유 크루는 이미 삭제된 상태").isLessThan(before.get("crew"));
            softly.assertThat(observedInTransaction.get().get("squad")).as("실패 시점에 스쿼드는 이미 삭제된 상태").isLessThan(before.get("squad"));
            softly.assertThat(snapshot()).isEqualTo(before);
        });
        assertUntouched(scenario);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("크루 삭제 단계에서 실패하면 앞서 삭제된 스쿼드와 하위 데이터까지 모두 원래대로 돌아온다")
    void rollsBackSquads_whenCrewDeletionFails() {
        Scenario scenario = createScenario();
        Map<String, Long> before = snapshot();
        AtomicReference<Map<String, Long>> observedInTransaction = new AtomicReference<>();
        doAnswer(invocation -> {
            observedInTransaction.set(snapshot());
            throw new DataAccessResourceFailureException(INJECTED_FAILURE);
        }).when(replaceDependency(crewContextDisposer, "crewRepository", CrewRepository.class)).deleteByIdIn(anyList());

        assertThatThrownBy(() -> memberCommandService.deleteMember(scenario.me().getId()))
                .isInstanceOf(DataAccessResourceFailureException.class)
                .hasMessage(INJECTED_FAILURE);

        assertSoftly(softly -> {
            softly.assertThat(observedInTransaction.get().get("squad")).as("실패 시점에 내 스쿼드는 이미 삭제된 상태").isLessThan(before.get("squad"));
            softly.assertThat(observedInTransaction.get().get("squadComment")).as("실패 시점에 스쿼드 댓글도 삭제된 상태").isLessThan(before.get("squadComment"));
            softly.assertThat(snapshot()).isEqualTo(before);
        });
        assertUntouched(scenario);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("스쿼드 멤버십 정리 단계에서 실패하면 앞서 삭제된 스쿼드와 감소시킨 인원수가 모두 원래대로 돌아온다")
    void rollsBackSquadCleanup_whenSquadMembershipCleanupFails() {
        Scenario scenario = createScenario();
        Map<String, Long> before = snapshot();
        AtomicReference<Map<String, Long>> observedInTransaction = new AtomicReference<>();
        doAnswer(invocation -> {
            observedInTransaction.set(snapshot());
            throw new DataAccessResourceFailureException(INJECTED_FAILURE);
        }).when(replaceDependency(memberContextDisposer, "squadMemberRepository", SquadMemberRepository.class)).deleteByMemberId(anyLong());

        assertThatThrownBy(() -> memberCommandService.deleteMember(scenario.me().getId()))
                .isInstanceOf(DataAccessResourceFailureException.class)
                .hasMessage(INJECTED_FAILURE);

        assertSoftly(softly -> {
            softly.assertThat(observedInTransaction.get().get("squad")).as("실패 시점에 내가 리더이거나 내 크루 소속인 스쿼드는 이미 삭제된 상태").isLessThan(before.get("squad"));
            softly.assertThat(snapshot()).isEqualTo(before);
        });
        assertUntouched(scenario);
    }

    private void assertUntouched(Scenario scenario) {
        assertSoftly(softly -> {
            softly.assertThat(memberJpaRepository.findById(scenario.me().getId())).as("탈퇴 대상 회원").isPresent();
            softly.assertThat(crewJpaRepository.findById(scenario.myCrew().getId())).as("내가 크루장인 크루").isPresent();
            softly.assertThat(crewJpaRepository.findById(scenario.otherCrew().getId()).get().getCurrentSize())
                    .as("내가 속한 다른 크루의 인원수").isEqualTo(scenario.otherCrewSize());
            softly.assertThat(squadJpaRepository.findById(scenario.otherSquad().getId()).get().getCurrentSize())
                    .as("내가 속한 다른 스쿼드의 인원수").isEqualTo(scenario.otherSquadSize());
            Announce announce = announceJpaRepository.findById(scenario.announce().getId()).get();
            softly.assertThat(announce.getMember()).as("다른 크루에 남긴 공지의 작성자").isNotNull();
            softly.assertThat(announce.getMember().getId()).isEqualTo(scenario.me().getId());
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T replaceDependency(Object bean, String fieldName, Class<T> type) {
        Object target = AopTestUtils.getUltimateTargetObject(bean);
        T original = (T) ReflectionTestUtils.getField(target, fieldName);
        T replacement = mock(type, delegatesTo(original));
        ReflectionTestUtils.setField(target, fieldName, replacement);
        restorations.add(() -> ReflectionTestUtils.setField(target, fieldName, original));
        return replacement;
    }

    private void executeScripts(String... scripts) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        for (String script : scripts) {
            populator.addScript(new ClassPathResource(script));
        }
        populator.execute(jdbcTemplate.getDataSource());
    }

    private Map<String, Long> snapshot() {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("member", memberJpaRepository.count());
        counts.put("crew", crewJpaRepository.count());
        counts.put("crewMember", crewMemberJpaRepository.count());
        counts.put("crewRequest", crewRequestJpaRepository.count());
        counts.put("crewHashtag", crewHashtagJpaRepository.count());
        counts.put("announce", announceJpaRepository.count());
        counts.put("squad", squadJpaRepository.count());
        counts.put("squadMember", squadMemberJpaRepository.count());
        counts.put("squadRequest", squadRequestJpaRepository.count());
        counts.put("squadComment", squadCommentJpaRepository.count());
        counts.put("squadCategory", squadCategoryJpaRepository.count());
        return counts;
    }

    private Scenario createScenario() {
        Member me = memberJpaRepository.save(createMember(1));
        Member otherOwner = memberJpaRepository.save(createMember(2));
        Member friend = memberJpaRepository.save(createMember(3));
        Member requester = memberJpaRepository.save(createMember(4));

        Crew myCrew = createCrew(me, "myCrew");
        myCrew.addCrewMember(CrewMemberFactory.general(myCrew, friend, LocalDateTime.now()));
        Crew savedMyCrew = crewJpaRepository.save(myCrew);
        crewHashtagJdbcRepository.insertBatch(savedMyCrew.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.ACTIVE, HashtagType.PASSIONATE)));
        crewRequestJpaRepository.save(CrewRequest.of(savedMyCrew, requester, LocalDateTime.now()));
        announceJpaRepository.save(new Announce("my title", "my content", savedMyCrew, me));

        Squad mySquad = createSquad(savedMyCrew, friend);
        mySquad.addMembers(createGeneralSquadMember(mySquad, me));
        Squad savedMySquad = squadJpaRepository.save(mySquad);
        squadCategoryJpaRepository.saveAll(createSquadCategories(savedMySquad, CategoryType.GAME));
        squadRequestJpaRepository.save(SquadRequest.of(savedMySquad, requester, LocalDateTime.now()));
        SquadComment myParent = squadCommentJpaRepository.save(SquadComment.create("my parent", savedMySquad, me));
        squadCommentJpaRepository.save(SquadComment.createReply(myParent, "friend reply", savedMySquad, friend));

        Crew otherCrew = createCrew(otherOwner, "otherCrew");
        otherCrew.addCrewMember(CrewMemberFactory.general(otherCrew, me, LocalDateTime.now()));
        Crew savedOtherCrew = crewJpaRepository.save(otherCrew);
        Announce announce = announceJpaRepository.save(new Announce("other title", "other content", savedOtherCrew, me));

        Squad otherSquad = createSquad(savedOtherCrew, otherOwner);
        otherSquad.addMembers(createGeneralSquadMember(otherSquad, me));
        Squad savedOtherSquad = squadJpaRepository.save(otherSquad);
        SquadComment otherParent = squadCommentJpaRepository.save(SquadComment.create("other parent", savedOtherSquad, me));
        squadCommentJpaRepository.save(SquadComment.createReply(otherParent, "owner reply", savedOtherSquad, otherOwner));

        long otherCrewSize = crewJpaRepository.findById(savedOtherCrew.getId()).get().getCurrentSize();
        int otherSquadSize = squadJpaRepository.findById(savedOtherSquad.getId()).get().getCurrentSize();
        return new Scenario(me, savedMyCrew, savedOtherCrew, savedOtherSquad, announce, otherCrewSize, otherSquadSize);
    }

    private record Scenario(Member me, Crew myCrew, Crew otherCrew, Squad otherSquad, Announce announce, long otherCrewSize, int otherSquadSize) {

    }
}
