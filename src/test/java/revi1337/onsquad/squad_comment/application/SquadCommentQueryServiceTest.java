package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad_comment.application.dto.SquadCommentDto;
import revi1337.onsquad.squad_comment.domain.SquadComment;
import revi1337.onsquad.squad_comment.domain.SquadCommentRepository;

class SquadCommentQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private SquadCommentRepository squadCommentRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private SquadRepository squadRepository;

    @Autowired
    private SquadCommentQueryService squadCommentQueryService;

    @Nested
    @DisplayName("초기 댓글 조회를 테스트한다.")
    class FetchInitialComments {

        @Test
        @DisplayName("초기 댓글 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_2", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_3", SQUAD, OWNER));
            SquadComment PARENT2 = squadCommentRepository.save(SquadComment.create("parent_2", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_4", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_5", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_6", SQUAD, OWNER));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);
            int CHILD_SIZE = 3;
            clearPersistenceContext();

            List<SquadCommentDto> comments = squadCommentQueryService
                    .fetchInitialComments(REVI.getId(), CREW.getId(), SQUAD.getId(), PAGE_REQUEST, CHILD_SIZE);

            assertThat(comments).hasSize(2);
            assertThat(comments.get(0).content()).isEqualTo("parent_2");
            assertThat(comments.get(0).replies()).hasSize(3);
            assertThat(comments.get(0).replies()).extracting("content")
                    .containsExactlyInAnyOrder("reply_4", "reply_5", "reply_6");
            assertThat(comments.get(1).content()).isEqualTo("parent_1");
            assertThat(comments.get(1).replies()).hasSize(3);
            assertThat(comments.get(1).replies()).extracting("content")
                    .containsExactlyInAnyOrder("reply_1", "reply_2", "reply_3");
        }

        @Test
        @DisplayName("부모 댓글 기준 페이징 & 각 부모댓글의 자식댓글 Limit 에 성공한다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_2", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_3", SQUAD, OWNER));
            SquadComment PARENT2 = squadCommentRepository.save(SquadComment.create("parent_2", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_4", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_5", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_6", SQUAD, OWNER));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 1);
            int CHILD_SIZE = 2;
            clearPersistenceContext();

            List<SquadCommentDto> comments = squadCommentQueryService
                    .fetchInitialComments(REVI.getId(), CREW.getId(), SQUAD.getId(), PAGE_REQUEST, CHILD_SIZE);

            assertThat(comments).hasSize(1);
            assertThat(comments.get(0).content()).isEqualTo("parent_2");
            assertThat(comments.get(0).replies()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("대댓글 조회를 테스트한다.")
    class FetchMoreChildren {

        @Test
        @DisplayName("대댓글 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_2", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_3", SQUAD, OWNER));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);

            List<SquadCommentDto> children = squadCommentQueryService
                    .fetchMoreChildren(REVI.getId(), CREW.getId(), SQUAD.getId(), PARENT1.getId(), PAGE_REQUEST);

            assertThat(children).hasSize(2);
            assertThat(children.get(0).content()).isEqualTo("reply_3");
            assertThat(children.get(1).content()).isEqualTo("reply_2");
        }
    }
}
