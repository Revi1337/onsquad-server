package revi1337.onsquad.comment.domain;

import com.blazebit.persistence.querydsl.JPQLNextExpressions;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.SQLExpressions;
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
    public Map<Comment, List<Comment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds, Pageable childPageable) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(comment.parent.id.in(parentIds))
                .orderBy(comment.createdAt.desc())
                .transform(groupBy(comment.parent).as(list(comment)));
    }

    @Override
    public List<Tuple> findGroupedChildCommentsByParentIdIn2(List<Long> parentIds) {
//        List<Tuple> sequence = jpaQueryFactory
//                .select(comment,
//                        SQLExpressions.rowNumber()
//                                .over()
//                                .partitionBy(comment.parent.id)
//                                .as("rn")
//                )
//                .from(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .where(
//                        comment.parent.id.in(parentIds)
//                )
//                .fetch();



//        List<Tuple> sequence = jpaQueryFactory
//                .select(comment,
//                        JPQLNextExpressions.rowNumber()
//                                .over()
//                                .partitionBy(comment.parent.id)
//                                .orderBy(comment.createdAt.desc())
//                                .as("rn")
//                )
//                .from(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .where(
//                        comment.parent.id.in(parentIds),
//                )
//                .fetch();



//        List<Tuple> sequence = jpaQueryFactory
//                .select(comment,
//                        JPQLNextExpressions.rowNumber()
//                                .over()
//                                .partitionBy(comment.parent.id)
//                                .orderBy(comment.createdAt.desc())
//                                .as("rn")
//                )
//                .from(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .where(
//                        comment.parent.id.in(parentIds)
//                )
//                .fetch();



//        List<Tuple> sequence = jpaQueryFactory
//                .select(comment,
//                        SQLExpressions.rowNumber()
//                                .over()
//                                .partitionBy(comment.parent.id)
//                                .orderBy(comment.createdAt.desc())
//                                .as("rn")
//                )
//                .from(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .where(
//                        comment.parent.id.in(parentIds)
//                )
//                .having(Expressions.numberPath(Long.class, "rn").loe(10))
//                .orderBy(Expressions.numberPath(Long.class, "rn").asc())
//                .fetch();

//        return sequence;
        return null;
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
