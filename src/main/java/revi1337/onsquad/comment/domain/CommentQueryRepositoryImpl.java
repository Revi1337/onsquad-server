package revi1337.onsquad.comment.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.comment.domain.vo.CommentType;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.*;
import static revi1337.onsquad.comment.domain.QComment.*;
import static revi1337.onsquad.comment.domain.vo.CommentType.*;
import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.member.domain.QMember.*;

public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    public CommentQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Comment> findCommentsByCrewName(Name crewName) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .leftJoin(comment.parent)
                .where(
                        commentTypeEq(CREW),
                        comment.crew.name.eq(crewName)
                )
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
                        commentTypeEq(CREW),
                        comment.crew.name.eq(crewName),
                        comment.parent.isNull()
                )
                .orderBy(comment.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Comment> findLimitedParentCommentsByCrewName(Name crewName, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(
                        commentTypeEq(CREW),
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
                .where(
                        commentTypeEq(CREW),
                        comment.parent.id.in(parentIds)
                )
                .orderBy(comment.createdAt.desc())
                .fetch();
    }

    @Override
    public Map<Comment, List<Comment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds) {
        return jpaQueryFactory
                .selectFrom(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(
                        commentTypeEq(CREW),
                        comment.parent.id.in(parentIds)
                )
                .orderBy(comment.createdAt.desc())
                .transform(groupBy(comment.parent).as(list(comment)));
    }

    @Override
    public List<Comment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize) {
        String sql = "SELECT * FROM (" +
                "    SELECT " +
                "        comment.*, " +
                "        ROW_NUMBER() OVER (PARTITION BY comment.parent_id ORDER BY comment.created_at DESC) AS rn " +
                "    FROM comment " +
                "    INNER JOIN member ON comment.member_id = member.id " +
                "    WHERE comment.type = :commentType AND comment.parent_id IN (:parentIds) " +
                ") AS subquery " +
                " WHERE subquery.rn <= (:childLimit)" +
                " ORDER BY subquery.rn ASC";

        List<Comment> resultList = entityManager.createNativeQuery(sql, Comment.class)
                .setParameter("commentType", CREW.toString())
                .setParameter("parentIds", parentIds)
                .setParameter("childLimit", childrenSize)
                .getResultList();

        Set<Long> memberIds = resultList.stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());

        entityManager.createQuery("select m from Member as m where m.id in :memberIds")
                .setParameter("memberIds", memberIds)
                .getResultList();

        return resultList;
    }

    @Override
    public Optional<Comment> findCommentById(Long commentId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(comment)
                        .innerJoin(comment.crew, crew).fetchJoin()
                        .where(
                                commentTypeEq(CREW),
                                comment.id.eq(commentId)
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression commentTypeEq(CommentType commentType) {
        return commentType != null ? comment.type.eq(commentType) : null;
    }
}
