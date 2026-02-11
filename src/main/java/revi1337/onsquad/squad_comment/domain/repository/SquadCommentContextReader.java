package revi1337.onsquad.squad_comment.domain.repository;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_comment.domain.entity.QSquadComment.squadComment;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.domain.entity.QMember;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentAddedContext;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentReplyAddedContext;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SquadCommentContextReader {

    private final QMember writer = new QMember("writer");
    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CommentAddedContext> readAddedContext(Long writerId, Long commentId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        CommentAddedContext.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        squad.id.as("squadId"),
                        squad.title.value.as("squadTitle"),
                        squad.member.id.as("squadMemberId"),
                        Expressions.as(Expressions.constant(commentId), "commentId"),
                        writer.id.as("commentWriterId"),
                        writer.nickname.value.as("commentWriterNickname")
                ))
                .from(squadComment)
                .innerJoin(squadComment.squad, squad)
                .innerJoin(squad.crew, crew)
                .innerJoin(writer).on(writer.id.eq(writerId))
                .where(squadComment.id.eq(commentId))
                .fetchOne()
        );
    }

    public Optional<CommentReplyAddedContext> readReplyAddedContext(Long parentId, Long writerId, Long replyId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        CommentReplyAddedContext.class,
                        crew.id.as("crewId"),
                        crew.name.value.as("crewName"),
                        squad.id.as("squadId"),
                        squad.title.value.as("squadTitle"),
                        squadComment.id.as("parentCommentId"),
                        squadComment.member.id.as("parentCommentWriterId"),
                        Expressions.as(Expressions.constant(replyId), "replyCommentId"),
                        writer.id.as("replyCommentWriterId"),
                        writer.nickname.value.as("replyCommentWriterNickname")
                ))
                .from(squadComment)
                .innerJoin(squadComment.squad, squad)
                .innerJoin(squad.crew, crew)
                .innerJoin(writer).on(writer.id.eq(writerId))
                .where(squadComment.id.eq(parentId))
                .fetchOne()
        );
    }
}
