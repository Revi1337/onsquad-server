package revi1337.onsquad.squad_comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;

import jakarta.persistence.PersistenceUnitUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadJpaRepository;

class SquadCommentJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentJpaRepository;

    @Test
    @DisplayName("댓글 ID, 스쿼드 ID, 크루 ID 로 댓글 조회에 성공한다. (1)")
    void findByIdAndSquadIdAndCrewId1() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
        SquadComment PARENT1 = squadCommentJpaRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
        clearPersistenceContext();

        Optional<SquadComment> parentComment = squadCommentJpaRepository.findByIdAndSquadIdAndCrewId(PARENT1.getId(), SQUAD.getId(), CREW.getId());

        assertThat(parentComment).isPresent();
    }

    @Test
    @DisplayName("댓글 ID, 스쿼드 ID, 크루 ID 로 댓글 조회에 성공한다. (2)")
    void findByIdAndSquadIdAndCrewId2() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
        Long DUMMY_PARENT_ID = 1L;
        clearPersistenceContext();

        Optional<SquadComment> parentComment = squadCommentJpaRepository.findByIdAndSquadIdAndCrewId(DUMMY_PARENT_ID, SQUAD.getId(), CREW.getId());

        assertThat(parentComment).isEmpty();
    }

    @Test
    @DisplayName("댓글 ID, 스쿼드 ID 로 댓글 조회에 성공한다. (1)")
    void findByIdAndSquadId() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
        SquadComment COMMENT = squadCommentJpaRepository.save(SquadComment.create("comment", SQUAD, OWNER));
        clearPersistenceContext();

        Optional<SquadComment> comment = squadCommentJpaRepository.findByIdAndSquadId(COMMENT.getId(), SQUAD.getId());

        assertThat(comment).isPresent();
    }

    @Test
    @DisplayName("댓글 ID, 스쿼드 ID 로 댓글 조회에 성공한다. (2)")
    void findByIdAndSquadId2() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
        SquadComment COMMENT = squadCommentJpaRepository.save(SquadComment.create("comment", SQUAD, OWNER));
        clearPersistenceContext();

        Optional<SquadComment> comment = squadCommentJpaRepository.findByIdAndSquadId(COMMENT.getId(), 100L);

        assertThat(comment).isEmpty();
    }

    @Test
    @DisplayName("댓글 ID, 스쿼드 ID 로 댓글과 스쿼드를 함께 조회한다. (1)")
    void findWithSquadByIdAndSquadId() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
        SquadComment COMMENT = squadCommentJpaRepository.save(SquadComment.create("comment", SQUAD, OWNER));
        clearPersistenceContext();
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        Optional<SquadComment> comment = squadCommentJpaRepository.findWithSquadByIdAndSquadId(COMMENT.getId(), SQUAD.getId());

        assertThat(comment).isPresent();
        assertThat(persistenceUnitUtil.isLoaded(comment.get().getSquad())).isTrue();
    }

    @Test
    @DisplayName("댓글 ID, 스쿼드 ID 로 댓글과 스쿼드를 함께 조회한다. (2)")
    void findWithSquadByIdAndSquadId2() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
        SquadComment COMMENT = squadCommentJpaRepository.save(SquadComment.create("comment", SQUAD, OWNER));
        clearPersistenceContext();

        Optional<SquadComment> comment = squadCommentJpaRepository.findWithSquadByIdAndSquadId(COMMENT.getId(), 100L);

        assertThat(comment).isEmpty();
    }
}
