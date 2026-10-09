package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadCategoryFixture.createSquadCategories;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceJpaRepository;
import revi1337.onsquad.category.domain.vo.CategoryType;
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
import revi1337.onsquad.hashtag.domain.HashtagType;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.event.MemberContextDisposed;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestJpaRepository;

@Sql({"/h2-hashtag.sql", "/h2-category.sql"})
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
    private SquadRequestJpaRepository squadRequestRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentRepository;

    @Autowired
    private SquadCategoryJpaRepository squadCategoryRepository;

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private MemberContextHandler contextHandler;

    @Nested
    @DisplayName("정상 케이스")
    class normal {

        @Test
        @DisplayName("사용자 자체가 탈퇴할때, 소유한 크루는 파기되고 타 크루에 남긴 흔적(신청, 멤버십)은 제거되며 공지사항 작성자 정보는 null 처리된다")
        void disposesOwnedCrewAndTracesInOtherCrews() {
            Member revi = createRevi();
            Member andong = createAndong();
            Member kwangwon = createKwangwon();
            memberRepository.saveAll(List.of(revi, andong, kwangwon));

            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            Crew savedCrew1 = crewRepository.save(crew);
            crewHashtagJdbcRepository.insertBatch(savedCrew1.getId(), createHashtags(HashtagType.ACTIVE, HashtagType.PASSIONATE));
            crewRequestRepository.save(createCrewRequest(savedCrew1, kwangwon));
            announceRepository.save(createCrewAnnounce(savedCrew1, revi));
            Announce crew1Announce2 = announceRepository.save(createCrewAnnounce(savedCrew1, andong));

            Crew savedCrew2 = crewRepository.save(createCrew(andong));
            crewHashtagJdbcRepository.insertBatch(savedCrew2.getId(), createHashtags(HashtagType.GAME_LOVER_FEMALE, HashtagType.CHALLENGING));
            crewRequestRepository.save(createCrewRequest(savedCrew2, revi));
            announceRepository.save(createCrewAnnounce(savedCrew2, andong));

            clearPersistenceContext();
            assertThat(crewRepository.findById(crew.getId()).get().getCurrentSize()).isEqualTo(2);

            contextHandler.disposeContext(andong);

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
                softly.assertThat(deletedAnnounceOpt.get().getMember()).isNull();

                clearPersistenceContext();
                softly.assertThat(crewRepository.findById(savedCrew1.getId()).get().getCurrentSize()).isOne();
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
        @DisplayName("크루장이 탈퇴하면 크루 하위의 해시태그, 신청, 멤버십, 공지, 스쿼드와 그 하위 데이터가 모두 삭제되고 다른 회원의 계정은 유지된다")
        void deletesEveryThingUnderCrew_whenOwnerWithdraws() {
            Member owner = memberRepository.save(createMember(1));
            Member leader = memberRepository.save(createMember(2));
            Member squadMember = memberRepository.save(createMember(3));
            Member requester = memberRepository.save(createMember(4));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createGeneralCrewMember(crew, leader));
            crew.addCrewMember(createGeneralCrewMember(crew, squadMember));
            Crew savedCrew = crewRepository.save(crew);
            crewHashtagJdbcRepository.insertBatch(savedCrew.getId(), createHashtags(HashtagType.ACTIVE, HashtagType.PASSIONATE));
            crewRequestRepository.save(createCrewRequest(savedCrew, requester));
            announceRepository.save(createCrewAnnounce(savedCrew, owner));
            announceRepository.save(createCrewAnnounce(savedCrew, leader));

            Squad squad = createSquad(savedCrew, leader);
            squad.addMembers(createGeneralSquadMember(squad, squadMember));
            Squad savedSquad = squadRepository.save(squad);
            squadCategoryRepository.saveAll(createSquadCategories(savedSquad, CategoryType.GAME, CategoryType.MANGACAFE));
            squadRequestRepository.save(SquadRequest.of(savedSquad, requester, LocalDateTime.now()));
            SquadComment parent = squadCommentRepository.save(SquadComment.create("comment", savedSquad, leader));
            squadCommentRepository.save(SquadComment.createReply(parent, "reply", savedSquad, squadMember));
            clearPersistenceContext();

            contextHandler.disposeContext(owner);

            clearPersistenceContext();
            assertSoftly(softly -> {
                softly.assertThat(crewRepository.findAll()).isEmpty();
                softly.assertThat(crewMemberRepository.findAll()).isEmpty();
                softly.assertThat(crewRequestRepository.findAll()).isEmpty();
                softly.assertThat(crewHashtagRepository.findAll()).isEmpty();
                softly.assertThat(announceRepository.findAll()).isEmpty();
                softly.assertThat(squadRepository.findAll()).isEmpty();
                softly.assertThat(squadMemberRepository.findAll()).isEmpty();
                softly.assertThat(squadRequestRepository.findAll()).isEmpty();
                softly.assertThat(squadCommentRepository.findAll()).isEmpty();
                softly.assertThat(squadCategoryRepository.findAll()).isEmpty();
                softly.assertThat(memberRepository.findById(owner.getId())).isEmpty();
                softly.assertThat(memberRepository.findAllById(List.of(leader.getId(), squadMember.getId(), requester.getId()))).hasSize(3);
            });
        }

        @Test
        @DisplayName("크루 일반 멤버인 스쿼드 리더가 탈퇴하면 그 스쿼드와 하위 데이터가 삭제되고 크루는 유지되며 인원수만 줄어든다")
        void deletesSquad_whenSquadLeaderWithdraws() {
            Member owner = memberRepository.save(createMember(1));
            Member leader = memberRepository.save(createMember(2));
            Member squadMember = memberRepository.save(createMember(3));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createGeneralCrewMember(crew, leader));
            crew.addCrewMember(createGeneralCrewMember(crew, squadMember));
            Crew savedCrew = crewRepository.save(crew);
            Squad squad = createSquad(savedCrew, leader);
            squad.addMembers(createGeneralSquadMember(squad, squadMember));
            Squad savedSquad = squadRepository.save(squad);
            squadCategoryRepository.saveAll(createSquadCategories(savedSquad, CategoryType.GAME));
            squadCommentRepository.save(SquadComment.create("comment", savedSquad, squadMember));
            Squad otherSquad = squadRepository.save(createSquad(savedCrew, owner));
            clearPersistenceContext();
            assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize()).isEqualTo(3);
            clearPersistenceContext();

            contextHandler.disposeContext(leader);

            clearPersistenceContext();
            assertSoftly(softly -> {
                softly.assertThat(memberRepository.findById(leader.getId())).isEmpty();
                softly.assertThat(squadRepository.findById(savedSquad.getId())).isEmpty();
                softly.assertThat(squadCategoryRepository.findAll()).isEmpty();
                softly.assertThat(squadCommentRepository.findAll()).isEmpty();
                softly.assertThat(squadMemberRepository.findAll().stream().map(sm -> sm.getSquad().getId()).toList())
                        .containsExactly(otherSquad.getId());
                softly.assertThat(crewRepository.findById(savedCrew.getId())).isPresent();
                softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize()).isEqualTo(2);
                softly.assertThat(memberRepository.findById(squadMember.getId())).isPresent();
            });
        }

        @Test
        @DisplayName("스쿼드 일반 멤버가 탈퇴하면 스쿼드는 유지되고 멤버십이 삭제되며 스쿼드 인원수가 줄고 잔여 인원이 늘어난다")
        void decrementsSquadSize_whenSquadGeneralMemberWithdraws() {
            Member owner = memberRepository.save(createMember(1));
            Member leader = memberRepository.save(createMember(2));
            Member member = memberRepository.save(createMember(3));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createGeneralCrewMember(crew, leader));
            crew.addCrewMember(createGeneralCrewMember(crew, member));
            Crew savedCrew = crewRepository.save(crew);
            Squad squad = createSquad(savedCrew, leader);
            squad.addMembers(createGeneralSquadMember(squad, member));
            Squad savedSquad = squadRepository.save(squad);
            clearPersistenceContext();
            Squad before = squadRepository.findById(savedSquad.getId()).get();
            int sizeBefore = before.getCurrentSize();
            int remainBefore = before.getRemain();
            clearPersistenceContext();

            contextHandler.disposeContext(member);

            clearPersistenceContext();
            Squad after = squadRepository.findById(savedSquad.getId()).get();
            assertSoftly(softly -> {
                softly.assertThat(memberRepository.findById(member.getId())).isEmpty();
                softly.assertThat(after.getCurrentSize()).isEqualTo(sizeBefore - 1);
                softly.assertThat(after.getRemain()).isEqualTo(remainBefore + 1);
                softly.assertThat(squadMemberRepository.findAll()).hasSize(1);
            });
        }

        @Test
        @DisplayName("스쿼드 가입 신청만 남긴 회원이 탈퇴하면 신청이 삭제되고 스쿼드는 유지된다")
        void deletesSquadRequests_whenRequesterWithdraws() {
            Member owner = memberRepository.save(createMember(1));
            Member requester = memberRepository.save(createMember(2));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createGeneralCrewMember(crew, requester));
            Crew savedCrew = crewRepository.save(crew);
            Squad savedSquad = squadRepository.save(createSquad(savedCrew, owner));
            squadRequestRepository.save(SquadRequest.of(savedSquad, requester, LocalDateTime.now()));
            clearPersistenceContext();

            contextHandler.disposeContext(requester);

            clearPersistenceContext();
            assertSoftly(softly -> {
                softly.assertThat(squadRequestRepository.findAll()).isEmpty();
                softly.assertThat(squadRepository.findById(savedSquad.getId())).isPresent();
                softly.assertThat(memberRepository.findById(requester.getId())).isEmpty();
            });
        }

        @Test
        @DisplayName("스쿼드에 댓글을 남긴 회원이 탈퇴하면 본인 댓글만 삭제되고 다른 회원의 댓글은 유지된다")
        void deletesOnlyOwnComments_whenCommenterWithdraws() {
            Member owner = memberRepository.save(createMember(1));
            Member commenter = memberRepository.save(createMember(2));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createGeneralCrewMember(crew, commenter));
            Crew savedCrew = crewRepository.save(crew);
            Squad squad = createSquad(savedCrew, owner);
            squad.addMembers(createGeneralSquadMember(squad, commenter));
            Squad savedSquad = squadRepository.save(squad);
            squadCommentRepository.save(SquadComment.create("owner comment", savedSquad, owner));
            squadCommentRepository.save(SquadComment.create("commenter comment", savedSquad, commenter));
            clearPersistenceContext();

            contextHandler.disposeContext(commenter);

            clearPersistenceContext();
            assertThat(squadCommentRepository.findAll()).extracting(SquadComment::getContent).containsExactly("owner comment");
        }

        @Test
        @DisplayName("다른 크루에 공지를 남긴 회원이 탈퇴하면 공지는 유지되고 작성자 정보만 null 처리된다")
        void nullifiesAnnounceWriter_whenWriterWithdraws() {
            Member owner = memberRepository.save(createMember(1));
            Member writer = memberRepository.save(createMember(2));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createManagerCrewMember(crew, writer));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, writer));
            clearPersistenceContext();

            contextHandler.disposeContext(writer);

            clearPersistenceContext();
            Optional<Announce> found = announceRepository.findById(announce.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getMember()).isNull();
        }

        @Test
        @DisplayName("탈퇴하면 회원 ID, 프로필 이미지, 다른 크루에 남긴 공지 참조를 담은 이벤트가 발행된다")
        void publishesMemberContextDisposed() {
            Member owner = memberRepository.save(createMember(1));
            Member writer = memberRepository.save(createMember(2));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createManagerCrewMember(crew, writer));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, writer));
            String profileImage = writer.getProfileImage();
            clearPersistenceContext();

            contextHandler.disposeContext(writer);

            List<MemberContextDisposed> published = events.stream(MemberContextDisposed.class).toList();
            assertThat(published).hasSize(1);
            MemberContextDisposed event = published.get(0);
            assertSoftly(softly -> {
                softly.assertThat(event.memberId()).isEqualTo(writer.getId());
                softly.assertThat(event.memberImageUrl()).isEqualTo(profileImage);
                softly.assertThat(event.announceReferences()).hasSize(1);
                softly.assertThat(event.announceReferences().get(0).crewId()).isEqualTo(savedCrew.getId());
                softly.assertThat(event.announceReferences().get(0).announceId()).isEqualTo(announce.getId());
            });
        }

        @Test
        @DisplayName("프로필 이미지가 없는 회원이 탈퇴하면 이벤트의 이미지 URL 은 null 이다")
        void publishesNullImage_whenMemberHasNoImage() {
            Member member = createMember(1);
            member.updateImage(null);
            memberRepository.save(member);
            clearPersistenceContext();

            contextHandler.disposeContext(member);

            List<MemberContextDisposed> published = events.stream(MemberContextDisposed.class).toList();
            assertThat(published).hasSize(1);
            assertThat(published.get(0).memberImageUrl()).isNull();
        }
    }

    @Nested
    @DisplayName("엣지 케이스")
    class edge {

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
        @DisplayName("여러 크루의 크루장이 탈퇴하면 소유한 크루가 모두 삭제된다")
        void deletesAllOwnedCrews_whenOwnerOfMultipleCrewsWithdraws() {
            Member owner = memberRepository.save(createMember(1));
            Member other = memberRepository.save(createMember(2));
            Crew crew1 = createCrew(owner, "crew1");
            crew1.addCrewMember(createGeneralCrewMember(crew1, other));
            Crew crew2 = createCrew(owner, "crew2");
            Crew saved1 = crewRepository.save(crew1);
            Crew saved2 = crewRepository.save(crew2);
            clearPersistenceContext();

            contextHandler.disposeContext(owner);

            clearPersistenceContext();
            assertSoftly(softly -> {
                softly.assertThat(crewRepository.findById(saved1.getId())).isEmpty();
                softly.assertThat(crewRepository.findById(saved2.getId())).isEmpty();
                softly.assertThat(crewMemberRepository.findAll()).isEmpty();
                softly.assertThat(memberRepository.findById(owner.getId())).isEmpty();
            });
        }

        @Test
        @DisplayName("크루장이면서 자기 크루 스쿼드의 리더이기도 하면 스쿼드가 중복 삭제되지 않고 정상적으로 탈퇴된다")
        void deletesSquadOnce_whenOwnerIsAlsoSquadLeaderOfOwnCrew() {
            Member owner = memberRepository.save(createMember(1));
            Crew savedCrew = crewRepository.save(createCrew(owner));
            Squad savedSquad = squadRepository.save(createSquad(savedCrew, owner));
            clearPersistenceContext();

            assertThatCode(() -> contextHandler.disposeContext(owner)).doesNotThrowAnyException();

            clearPersistenceContext();
            assertSoftly(softly -> {
                softly.assertThat(crewRepository.findById(savedCrew.getId())).isEmpty();
                softly.assertThat(squadRepository.findById(savedSquad.getId())).isEmpty();
                softly.assertThat(memberRepository.findById(owner.getId())).isEmpty();
            });
        }

        @Test
        @DisplayName("한 크루의 크루장이면서 다른 크루의 일반 멤버이면 내 크루만 삭제되고 다른 크루는 유지되며 인원수가 줄어든다")
        void deletesOnlyOwnedCrew_whenOwnerIsAlsoMemberOfAnotherCrew() {
            Member me = memberRepository.save(createMember(1));
            Member other = memberRepository.save(createMember(2));
            Crew ownCrew = crewRepository.save(createCrew(me, "ownCrew"));
            Crew otherCrew = createCrew(other, "otherCrew");
            otherCrew.addCrewMember(createGeneralCrewMember(otherCrew, me));
            Crew savedOtherCrew = crewRepository.save(otherCrew);
            clearPersistenceContext();

            contextHandler.disposeContext(me);

            clearPersistenceContext();
            assertSoftly(softly -> {
                softly.assertThat(crewRepository.findById(ownCrew.getId())).isEmpty();
                softly.assertThat(crewRepository.findById(savedOtherCrew.getId())).isPresent();
                softly.assertThat(crewRepository.findById(savedOtherCrew.getId()).get().getCurrentSize()).isOne();
                softly.assertThat(crewMemberRepository.findAll()).hasSize(1);
            });
        }

        @Test
        @DisplayName("탈퇴 대상과 무관한 크루, 스쿼드, 댓글, 회원은 변경되지 않는다")
        void doesNotTouchUnrelatedData() {
            Member me = memberRepository.save(createMember(1));
            Member unrelatedOwner = memberRepository.save(createMember(2));
            Member unrelatedMember = memberRepository.save(createMember(3));
            Crew myCrew = crewRepository.save(createCrew(me, "myCrew"));
            Crew unrelatedCrew = createCrew(unrelatedOwner, "unrelatedCrew");
            unrelatedCrew.addCrewMember(createGeneralCrewMember(unrelatedCrew, unrelatedMember));
            Crew savedUnrelatedCrew = crewRepository.save(unrelatedCrew);
            Squad unrelatedSquad = createSquad(savedUnrelatedCrew, unrelatedOwner);
            unrelatedSquad.addMembers(createGeneralSquadMember(unrelatedSquad, unrelatedMember));
            Squad savedUnrelatedSquad = squadRepository.save(unrelatedSquad);
            squadCommentRepository.save(SquadComment.create("unrelated", savedUnrelatedSquad, unrelatedMember));
            announceRepository.save(createCrewAnnounce(savedUnrelatedCrew, unrelatedOwner));
            clearPersistenceContext();
            long crewSizeBefore = crewRepository.findById(savedUnrelatedCrew.getId()).get().getCurrentSize();
            int squadSizeBefore = squadRepository.findById(savedUnrelatedSquad.getId()).get().getCurrentSize();
            clearPersistenceContext();

            contextHandler.disposeContext(me);

            clearPersistenceContext();
            assertSoftly(softly -> {
                softly.assertThat(crewRepository.findById(myCrew.getId())).isEmpty();
                softly.assertThat(crewRepository.findById(savedUnrelatedCrew.getId()).get().getCurrentSize()).isEqualTo(crewSizeBefore);
                softly.assertThat(squadRepository.findById(savedUnrelatedSquad.getId()).get().getCurrentSize()).isEqualTo(squadSizeBefore);
                softly.assertThat(squadCommentRepository.findAll()).hasSize(1);
                softly.assertThat(announceRepository.findAll()).hasSize(1);
                softly.assertThat(crewMemberRepository.findAll()).hasSize(2);
                softly.assertThat(memberRepository.findAllById(List.of(unrelatedOwner.getId(), unrelatedMember.getId()))).hasSize(2);
            });
        }

        @Test
        @DisplayName("내 부모 댓글에 다른 회원의 답글이 달려 있어도 탈퇴되고, 내 댓글과 그 답글이 함께 삭제되며 무관한 댓글은 유지된다")
        void deletesReplyThreads_whenParentCommentWriterWithdraws() {
            Member owner = memberRepository.save(createMember(1));
            Member me = memberRepository.save(createMember(2));
            Member other = memberRepository.save(createMember(3));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createGeneralCrewMember(crew, me));
            crew.addCrewMember(createGeneralCrewMember(crew, other));
            Crew savedCrew = crewRepository.save(crew);
            Squad squad = createSquad(savedCrew, owner);
            squad.addMembers(createGeneralSquadMember(squad, me));
            squad.addMembers(createGeneralSquadMember(squad, other));
            Squad savedSquad = squadRepository.save(squad);

            SquadComment myParent = squadCommentRepository.save(SquadComment.create("my parent", savedSquad, me));
            squadCommentRepository.save(SquadComment.createReply(myParent, "other reply to my parent", savedSquad, other));
            squadCommentRepository.save(SquadComment.createReply(myParent, "my reply to my parent", savedSquad, me));
            SquadComment otherParent = squadCommentRepository.save(SquadComment.create("other parent", savedSquad, other));
            squadCommentRepository.save(SquadComment.createReply(otherParent, "my reply to other parent", savedSquad, me));
            squadCommentRepository.save(SquadComment.createReply(otherParent, "other reply to other parent", savedSquad, other));
            clearPersistenceContext();

            contextHandler.disposeContext(me);

            clearPersistenceContext();
            assertSoftly(softly -> {
                softly.assertThat(memberRepository.findById(me.getId())).isEmpty();
                softly.assertThat(squadCommentRepository.findAll()).extracting(SquadComment::getContent)
                        .containsExactlyInAnyOrder("other parent", "other reply to other parent");
            });
        }

        @Test
        @DisplayName("이미 탈퇴 처리된 회원으로 다시 호출해도 예외가 발생하지 않는다")
        void doesNotThrow_whenMemberAlreadyDisposed() {
            Member member = memberRepository.save(createMember(1));
            clearPersistenceContext();
            contextHandler.disposeContext(member);
            clearPersistenceContext();

            assertThatCode(() -> contextHandler.disposeContext(member)).doesNotThrowAnyException();
        }
    }

    private List<Hashtag> createHashtags(HashtagType... hashtagTypes) {
        return Hashtag.fromHashtagTypes(Arrays.asList(hashtagTypes));
    }

    private CrewRequest createCrewRequest(Crew crew, Member member) {
        return CrewRequest.of(crew, member, LocalDateTime.now());
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    private Announce createCrewAnnounce(Crew crew, Member member) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, member);
    }
}
