package revi1337.onsquad.comment.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.comment.domain.QComment.*;
import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.member.domain.QMember.*;

@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findCommentsByCrewName(Name crewName) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .leftJoin(comment.parent)
                .where(comment.crew.name.eq(crewName))
                .orderBy(
                        comment.parent.id.asc().nullsFirst(),
                        comment.createdAt.desc()
                )
                .fetch();
    }

    @Override
    public Optional<Comment> findCommentById(Long commentId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(comment)
                        .innerJoin(comment.crew, crew).fetchJoin()
                        .where(comment.id.eq(commentId))
                        .fetchOne()
        );
    }
}
