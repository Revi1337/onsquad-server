package revi1337.onsquad.squad_comment.application.notification;

import static revi1337.onsquad.crew.domain.entity.QCrew.crew;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;
import static revi1337.onsquad.squad_comment.domain.entity.QSquadComment.squadComment;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.domain.entity.QMember;
import revi1337.onsquad.squad_comment.application.notification.CommentNotificationFetchResult.CommentAddedNotificationResult;
import revi1337.onsquad.squad_comment.application.notification.CommentNotificationFetchResult.CommentReplyAddedNotificationResult;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentNotificationFetcher {

    private final QMember writer = new QMember("writer");
    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CommentAddedNotificationResult> fetchAddedInformation(Long writerId, Long commentId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        CommentAddedNotificationResult.class,
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

    public Optional<CommentReplyAddedNotificationResult> fetchReplyAddedInformation(Long parentId, Long writerId, Long replyId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(
                        CommentReplyAddedNotificationResult.class,
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
