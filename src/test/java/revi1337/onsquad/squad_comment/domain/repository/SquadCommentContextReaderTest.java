package revi1337.onsquad.squad_comment.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentAddedContext;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentReplyAddedContext;

@Import(SquadCommentContextReader.class)
class SquadCommentContextReaderTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentRepository;

    @Autowired
    private SquadCommentContextReader contextReader;

    @Test
    @DisplayName("댓글 추가 시, 알림 및 이력 생성에 필요한 스쿼드, 크루, 작성자 정보가 포함된 컨텍스트를 조회한다.")
    void readAddedContext() {
        Member leader = memberRepository.save(createMember("리더"));
        Member writerMember = memberRepository.save(createMember("작성자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        SquadComment comment = squadCommentRepository.save(createSquadComment(squad, writerMember, false));
        clearPersistenceContext();

        Optional<CommentAddedContext> contextOpt = contextReader.readAddedContext(writerMember.getId(), comment.getId());

        assertSoftly(softly -> {
            assertThat(contextOpt).isPresent();
            CommentAddedContext context = contextOpt.get();
            softly.assertThat(context.crewId()).isEqualTo(crew.getId());
            softly.assertThat(context.crewName()).isEqualTo("우리 크루");
            softly.assertThat(context.squadId()).isEqualTo(squad.getId());
            softly.assertThat(context.squadTitle()).isEqualTo("신규 스쿼드");
            softly.assertThat(context.squadMemberId()).isEqualTo(leader.getId());
            softly.assertThat(context.commentId()).isEqualTo(comment.getId());
            softly.assertThat(context.commentWriterId()).isEqualTo(writerMember.getId());
            softly.assertThat(context.commentWriterNickname()).isEqualTo("작성자");
        });
    }

    @Test
    @DisplayName("대댓글 추가 시, 부모 댓글 정보와 대댓글 작성자 정보가 포함된 컨텍스트를 조회한다.")
    void readReplyAddedContext() {
        Member leader = memberRepository.save(createMember("리더"));
        Member parentWriter = memberRepository.save(createMember("부모작성자"));
        Member replyWriter = memberRepository.save(createMember("대댓글작성자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        SquadComment parentComment = squadCommentRepository.save(createSquadComment(squad, parentWriter, false));
        SquadComment replyComment = squadCommentRepository.save(createSquadCommentReply(parentComment, squad, replyWriter, false));
        clearPersistenceContext();

        Optional<CommentReplyAddedContext> contextOpt = contextReader.readReplyAddedContext(
                parentComment.getId(),
                replyWriter.getId(),
                replyComment.getId()
        );

        assertSoftly(softly -> {
            assertThat(contextOpt).isPresent();
            CommentReplyAddedContext context = contextOpt.get();
            softly.assertThat(context.crewId()).isEqualTo(crew.getId());
            softly.assertThat(context.crewName()).isEqualTo("우리 크루");
            softly.assertThat(context.squadId()).isEqualTo(squad.getId());
            softly.assertThat(context.squadTitle()).isEqualTo("신규 스쿼드");
            softly.assertThat(context.parentCommentId()).isEqualTo(parentComment.getId());
            softly.assertThat(context.parentCommentWriterId()).isEqualTo(parentWriter.getId());
            softly.assertThat(context.replyCommentId()).isEqualTo(replyComment.getId());
            softly.assertThat(context.replyCommentWriterId()).isEqualTo(replyWriter.getId());
            softly.assertThat(context.replyCommentWriterNickname()).isEqualTo("대댓글작성자");
        });
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
