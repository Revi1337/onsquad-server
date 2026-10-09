package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceJpaRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagJdbcRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestJpaRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.hashtag.domain.HashtagType;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;

@Sql({"/h2-hashtag.sql"}) // TODO 매우 중요한 비지니스 로직(다른 도메인 테스트 다 짜고 통합으로 진행해야할듯)
class MemberContextHandlerTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewHashtagJdbcRepository crewHashtagJdbcRepository;

    @Autowired
    private CrewHashtagJpaRepository crewHashtagRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Autowired
    private AnnounceJpaRepository announceRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberRepository;

    @Autowired
    private MemberContextHandler contextHandler;

    @Test
    @DisplayName("사용자 자체가 탈퇴할때, 소유한 크루는 파기되고 타 크루에 남긴 흔적(신청, 멤버십)은 제거되며 공지사항 작성자 정보는 null 처리된다")
    void disposeContext() {
        Member revi = createRevi();
        Member andong = createAndong();
        Member kwangwon = createKwangwon();
        memberRepository.saveAll(List.of(revi, andong, kwangwon));

        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        Crew savedCrew1 = crewRepository.save(crew);
        crewHashtagJdbcRepository.insertBatch(savedCrew1.getId(), createHashtags(HashtagType.ACTIVE, HashtagType.PASSIONATE));
        crewRequestRepository.save(createCrewRequest(savedCrew1, kwangwon));
        Announce crew1Announce1 = announceRepository.save(createCrewAnnounce(savedCrew1, revi));
        Announce crew1Announce2 = announceRepository.save(createCrewAnnounce(savedCrew1, andong));

        Crew savedCrew2 = crewRepository.save(createCrew(andong));
        crewHashtagJdbcRepository.insertBatch(savedCrew2.getId(), createHashtags(HashtagType.GAME_LOVER_FEMALE, HashtagType.CHALLENGING));
        crewRequestRepository.save(createCrewRequest(savedCrew2, revi));
        announceRepository.save(createCrewAnnounce(savedCrew2, andong));

        clearPersistenceContext();
        assertThat(crewRepository.findById(crew.getId()).get().getCurrentSize()).isEqualTo(2);

        // when
        contextHandler.disposeContext(andong);

        // then
        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(crewHashtagRepository.findAll()).hasSize(2);
            softly.assertThat(crewRequestRepository.findAll()).hasSize(1);
            softly.assertThat(crewMemberRepository.findAll().size()).isOne();
            softly.assertThat(announceRepository.findAll()).hasSize(2);
            softly.assertThat(crewRepository.findAll().size()).isOne();

            clearPersistenceContext();
            Optional<Announce> deletedAnnounceOpt = announceRepository.findById(crew1Announce2.getId());
            softly.assertThat(deletedAnnounceOpt).isPresent();
            Announce deletedAnnounce = deletedAnnounceOpt.get();
            softly.assertThat(deletedAnnounce.getMember()).isNull();

            clearPersistenceContext();
            softly.assertThat(crewRepository.findById(savedCrew1.getId()).get().getCurrentSize()).isOne();
        });
    }

    @Test
    @DisplayName("어떤 크루에도 속하지 않은 회원도 예외 없이 탈퇴할 수 있다")
    void disposesContext_whenMemberHasNoCrew() {
        Member member = memberRepository.save(createMember(1));
        clearPersistenceContext();

        contextHandler.disposeContext(member);

        clearPersistenceContext();
        assertThat(memberRepository.findById(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("혼자뿐인 크루의 크루장이 탈퇴하면 크루가 삭제되고 예외 없이 탈퇴된다")
    void deletesCrewAndMember_whenOwnerIsOnlyMember() {
        Member owner = memberRepository.save(createMember(1));
        Crew crew = crewRepository.save(createCrew(owner));
        clearPersistenceContext();

        contextHandler.disposeContext(owner);

        clearPersistenceContext();
        assertSoftly(softly -> {
            softly.assertThat(memberRepository.findById(owner.getId())).isEmpty();
            softly.assertThat(crewRepository.findById(crew.getId())).isEmpty();
            softly.assertThat(crewMemberRepository.findAll()).isEmpty();
        });
    }

    @Test
    @DisplayName("다른 멤버가 있는 크루의 유일한 크루장이 탈퇴하면 크루와 멤버십이 삭제되고 다른 멤버의 계정은 유지된다")
    void deletesCrewWithOtherMembers_whenOwnerWithdraws() {
        Member owner = memberRepository.save(createMember(1));
        Member other = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, other));
        Crew savedCrew = crewRepository.save(crew);
        clearPersistenceContext();

        contextHandler.disposeContext(owner);

        clearPersistenceContext();
        assertSoftly(softly -> {
            softly.assertThat(memberRepository.findById(owner.getId())).isEmpty();
            softly.assertThat(crewRepository.findById(savedCrew.getId())).isEmpty();
            softly.assertThat(crewMemberRepository.findAll()).isEmpty();
            softly.assertThat(memberRepository.findById(other.getId())).isPresent();
        });
    }

    @Test
    @DisplayName("크루 가입 신청만 남긴 회원도 예외 없이 탈퇴되고 신청이 제거된다")
    void deletesRequests_whenMemberOnlyRequestedToJoin() {
        Member owner = memberRepository.save(createMember(1));
        Member requester = memberRepository.save(createMember(2));
        Crew crew = crewRepository.save(createCrew(owner));
        crewRequestRepository.save(createCrewRequest(crew, requester));
        clearPersistenceContext();

        contextHandler.disposeContext(requester);

        clearPersistenceContext();
        assertSoftly(softly -> {
            softly.assertThat(memberRepository.findById(requester.getId())).isEmpty();
            softly.assertThat(crewRequestRepository.findAll()).isEmpty();
            softly.assertThat(crewRepository.findById(crew.getId())).isPresent();
        });
    }

    @Test
    @DisplayName("유일한 크루의 크루장이 탈퇴하면 크루와 하위 스쿼드가 삭제되고 계정도 삭제된다")
    void deletesCrewAndSquads_whenOwnerWithdraws() {
        Member owner = memberRepository.save(createMember(1));
        Member other = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, other));
        Crew savedCrew = crewRepository.save(crew);
        Squad savedSquad = squadRepository.save(createSquad(savedCrew, other));
        clearPersistenceContext();

        contextHandler.disposeContext(owner);

        clearPersistenceContext();
        assertSoftly(softly -> {
            softly.assertThat(memberRepository.findById(owner.getId())).isEmpty();
            softly.assertThat(crewRepository.findById(savedCrew.getId())).isEmpty();
            softly.assertThat(squadRepository.findById(savedSquad.getId())).isEmpty();
            softly.assertThat(squadMemberRepository.findAll()).isEmpty();
            softly.assertThat(memberRepository.findById(other.getId())).isPresent();
        });
    }

    @Test
    @DisplayName("크루원인 회원이 탈퇴하면 계정이 삭제되고 크루는 유지되며 인원수만 줄어든다")
    void decrementsCrewSize_whenCrewMemberWithdraws() {
        Member owner = memberRepository.save(createMember(1));
        Member member = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, member));
        Crew savedCrew = crewRepository.save(crew);
        clearPersistenceContext();
        assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize()).isEqualTo(2);
        clearPersistenceContext();

        contextHandler.disposeContext(member);

        clearPersistenceContext();
        assertSoftly(softly -> {
            softly.assertThat(memberRepository.findById(member.getId())).isEmpty();
            softly.assertThat(crewRepository.findById(savedCrew.getId())).isPresent();
            softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize()).isOne();
            softly.assertThat(crewMemberRepository.findAll()).hasSize(1);
            softly.assertThat(memberRepository.findById(owner.getId())).isPresent();
        });
    }

    private List<Hashtag> createHashtags(HashtagType... hashtagTypes) {
        return Hashtag.fromHashtagTypes(Arrays.asList(hashtagTypes));
    }

    private CrewRequest createCrewRequest(Crew crew, Member andong) {
        return CrewRequest.of(crew, andong, LocalDateTime.now());
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private Announce createCrewAnnounce(Crew crew, Member revi) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, revi);
    }
}
