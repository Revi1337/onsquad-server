package revi1337.onsquad.comment.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.querydsl.core.group.GroupBy.*;
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

    public List<Comment> findParentCommentsByCrewName(Name crewName) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(
                        comment.crew.name.eq(crewName),
                        comment.parent.isNull()
                )
                .orderBy(comment.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Comment> findParentCommentsByCrewNameUsingPageable(Name crewName, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(
                        comment.crew.name.eq(crewName),
                        comment.parent.isNull()
                )
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Comment> findChildCommentsByParentIdIn(List<Long> parentIds) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(comment.parent.id.in(parentIds))
                .orderBy(comment.createdAt.desc())
                .fetch();
    }

    @Override
    public Map<Comment, List<Comment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(comment.parent.id.in(parentIds))
                .orderBy(comment.createdAt.desc())
                .transform(groupBy(comment.parent).as(list(comment)));
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
