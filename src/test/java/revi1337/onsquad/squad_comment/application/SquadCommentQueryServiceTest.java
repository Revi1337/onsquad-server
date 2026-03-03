package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_comment.application.response.SquadCommentResponse;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentJpaRepository;

class SquadCommentQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentRepository;

    @Autowired
    private SquadCommentQueryService squadCommentQueryService;

    @Nested
    @DisplayName("초기 부모 댓글 목록 조회")
    class fetchInitialComments {

        @Test
        @DisplayName("스쿼드 리더가 조회할 경우, 본인이 쓰지 않은 댓글도 삭제 권한(canDelete)이 true로 반환된다.")
        void successAsSquadLeader() {
            Member leaderMember = memberRepository.save(createMember("리더"));
            Member writerMember = memberRepository.save(createMember("작성자"));
            Crew crew = crewRepository.save(createCrew(leaderMember, "우리 크루"));
            Squad squad = squadRepository.save(createSquad(crew, leaderMember, "신규 스쿼드"));
            squadCommentRepository.save(createSquadComment(squad, writerMember, false));
            squadCommentRepository.save(createSquadComment(squad, writerMember, false));
            clearPersistenceContext();
            PageRequest pageRequest = PageRequest.of(0, 10);

            PageResponse<SquadCommentResponse> response =
                    squadCommentQueryService.fetchInitialComments(leaderMember.getId(), squad.getId(), pageRequest);

            assertSoftly(softly -> {
                softly.assertThat(response.totalCount()).isEqualTo(2);
                softly.assertThat(response.results().get(0).states().canDelete()).isTrue();
                softly.assertThat(response.results().get(1).states().canDelete()).isTrue();
            });
        }

        @Test
        @DisplayName("스쿼드 미참여자(단순 크루원)가 조회할 경우, 모든 댓글의 삭제 권한이 false로 반환된다.")
        void successAsCrewMember() {
            Member leader = memberRepository.save(createMember("리더"));
            Member crewMember = memberRepository.save(createMember("크루원"));
            Member writer = memberRepository.save(createMember("작성자"));
            Crew crew = createCrew(leader, "우리 크루");
            crew.addCrewMember(createGeneralCrewMember(crew, crewMember));
            crewRepository.save(crew);
            Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
            squadCommentRepository.save(createSquadComment(squad, writer, false));
            clearPersistenceContext();
            PageRequest pageRequest = PageRequest.of(0, 10);

            PageResponse<SquadCommentResponse> response =
                    squadCommentQueryService.fetchInitialComments(crewMember.getId(), squad.getId(), pageRequest);

            assertSoftly(softly -> {
                softly.assertThat(response.totalCount()).isEqualTo(1);
                softly.assertThat(response.results().get(0).states().canDelete()).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("답글(대댓글) 더보기 조회")
    class fetchMoreChildren {

        @Test
        @DisplayName("특정 부모 댓글의 답글 목록을 페이징하여 조회한다.")
        void success() {
            Member leader = memberRepository.save(createMember("리더"));
            Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
            Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
            SquadComment parent = squadCommentRepository.save(createSquadComment(squad, leader, false));
            squadCommentRepository.save(createSquadCommentReply(parent, squad, leader, false));
            squadCommentRepository.save(createSquadCommentReply(parent, squad, leader, false));
            clearPersistenceContext();
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());

            PageResponse<SquadCommentResponse> response =
                    squadCommentQueryService.fetchMoreChildren(leader.getId(), squad.getId(), parent.getId(), pageRequest);

            assertSoftly(softly -> {
                softly.assertThat(response.totalCount()).isEqualTo(2);
                softly.assertThat(response.results().get(0).parentId()).isEqualTo(parent.getId());
            });
        }

        @Test
        @DisplayName("조회하려는 부모 댓글이 해당 스쿼드에 속하지 않으면 예외가 발생한다.")
        void failMismatchSquad() {
            Member leader = memberRepository.save(createMember("리더"));
            Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
            Squad squad1 = squadRepository.save(createSquad(crew, leader, "스쿼드1"));
            Squad squad2 = squadRepository.save(createSquad(crew, leader, "스쿼드2"));
            SquadComment parentInSquad1 = squadCommentRepository.save(createSquadComment(squad1, leader, false));
            clearPersistenceContext();
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());

            assertThatThrownBy(() -> squadCommentQueryService.fetchMoreChildren(leader.getId(), squad2.getId(), parentInSquad1.getId(), pageRequest));
        }
    }

    private static CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    public static SquadComment createSquadComment(Squad squad, Member member, boolean deleted) {
        SquadComment comment = SquadComment.create(UUID.randomUUID().toString(), squad, member);
        ReflectionTestUtils.setField(comment, "deleted", deleted);
        return comment;
    }

    public static SquadComment createSquadCommentReply(SquadComment parent, Squad squad, Member member, boolean deleted) {
        SquadComment reply = SquadComment.createReply(parent, UUID.randomUUID().toString(), squad, member);
        ReflectionTestUtils.setField(reply, "deleted", deleted);
        return reply;
    }
}
