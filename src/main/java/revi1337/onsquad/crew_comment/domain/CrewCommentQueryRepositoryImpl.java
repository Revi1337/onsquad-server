package revi1337.onsquad.crew_comment.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_comment.domain.QCrewComment.*;
import static revi1337.onsquad.member.domain.QMember.member;

public class CrewCommentQueryRepositoryImpl implements CrewCommentQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    public CrewCommentQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<CrewComment> findCommentsByCrewName(Name crewName) {
        return jpaQueryFactory
                .selectFrom(crewComment)
                .innerJoin(crewComment.member, member).fetchJoin()
                .innerJoin(crewComment.crew, crew).fetchJoin()
                .leftJoin(crewComment.parent)
                .where(crewComment.crew.name.eq(crewName))
                .orderBy(
                        crewComment.parent.id.asc().nullsFirst(),
                        crewComment.createdAt.desc()
                )
                .fetch();
    }

    public List<CrewComment> findParentCommentsByCrewName(Name crewName) {
        return jpaQueryFactory
                .selectFrom(crewComment)
                .innerJoin(crewComment.member, member).fetchJoin()
                .innerJoin(crewComment.crew, crew).fetchJoin()
                .where(
                        crewComment.crew.name.eq(crewName),
                        crewComment.parent.isNull()
                )
                .orderBy(crewComment.createdAt.desc())
                .fetch();
    }

    @Override
    public List<CrewComment> findLimitedParentCommentsByCrewName(Name crewName, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(crewComment)
                .innerJoin(crewComment.member, member).fetchJoin()
                .innerJoin(crewComment.crew, crew).fetchJoin()
                .where(
                        crewComment.crew.name.eq(crewName),
                        crewComment.parent.isNull()
                )
                .orderBy(crewComment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<CrewComment> findChildCommentsByParentIdIn(List<Long> parentIds) {
        return jpaQueryFactory
                .selectFrom(crewComment)
                .innerJoin(crewComment.member, member).fetchJoin()
                .innerJoin(crewComment.crew, crew).fetchJoin()
                .where(crewComment.parent.id.in(parentIds))
                .orderBy(crewComment.createdAt.desc())
                .fetch();
    }

    @Override
    public Map<CrewComment, List<CrewComment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds) {
        return jpaQueryFactory
                .selectFrom(crewComment)
                .innerJoin(crewComment.member, member).fetchJoin()
                .innerJoin(crewComment.crew, crew).fetchJoin()
                .where(crewComment.parent.id.in(parentIds))
                .orderBy(crewComment.createdAt.desc())
                .transform(groupBy(crewComment.parent).as(list(crewComment)));
    }

    @Override
    public List<CrewComment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize) {
        String sql = "SELECT * FROM (" +
                "    SELECT " +
                "        crew_comment.*, " +
                "        ROW_NUMBER() OVER (PARTITION BY crew_comment.parent_id ORDER BY crew_comment.created_at DESC) AS rn " +
                "    FROM crew_comment " +
                "    INNER JOIN member ON crew_comment.member_id = member.id " +
                "    WHERE crew_comment.parent_id IN (:parentIds) " +
                ") AS subquery " +
                " WHERE subquery.rn <= (:childLimit)" +
                " ORDER BY subquery.rn ASC";

        List<CrewComment> resultList = entityManager.createNativeQuery(sql, CrewComment.class)
                .setParameter("parentIds", parentIds)
                .setParameter("childLimit", childrenSize)
                .getResultList();

        Set<Long> memberIds = resultList.stream()
                .map(CrewComment::getId)
                .collect(Collectors.toSet());

        entityManager.createQuery("select m from Member as m where m.id in :memberIds")
                .setParameter("memberIds", memberIds)
                .getResultList();

        return resultList;
    }

    @Override
    public Optional<CrewComment> findCommentById(Long commentId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(crewComment)
                        .innerJoin(crewComment.crew, crew).fetchJoin()
                        .where(crewComment.id.eq(commentId))
                        .fetchOne()
        );
    }
}






//package revi1337.onsquad.crew_comment.domain;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.springframework.data.domain.Pageable;
//import revi1337.onsquad.crew.domain.vo.Name;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static com.querydsl.core.group.GroupBy.groupBy;
//import static com.querydsl.core.group.GroupBy.list;
//import static revi1337.onsquad.crew.domain.QCrew.crew;
//import static revi1337.onsquad.member.domain.QMember.member;
//
//public class CrewCommentQueryRepositoryImpl implements CrewCommentQueryRepository {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private final JPAQueryFactory jpaQueryFactory;
//
//    public CrewCommentQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
//        this.jpaQueryFactory = jpaQueryFactory;
//    }
//
//    @Override
//    public List<CrewComment> findCommentsByCrewName(Name crewName) {
//        return jpaQueryFactory
//                .selectFrom(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .leftJoin(comment.parent)
//                .where(
//                        commentTypeEq(CREW),
//                        comment.crew.name.eq(crewName)
//                )
//                .orderBy(
//                        comment.parent.id.asc().nullsFirst(),
//                        comment.createdAt.desc()
//                )
//                .fetch();
//    }
//
//    public List<CrewComment> findParentCommentsByCrewName(Name crewName) {
//        return jpaQueryFactory
//                .selectFrom(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .where(
//                        commentTypeEq(CREW),
//                        comment.crew.name.eq(crewName),
//                        comment.parent.isNull()
//                )
//                .orderBy(comment.createdAt.desc())
//                .fetch();
//    }
//
//    @Override
//    public List<CrewComment> findLimitedParentCommentsByCrewName(Name crewName, Pageable pageable) {
//        return jpaQueryFactory
//                .selectFrom(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .where(
//                        commentTypeEq(CREW),
//                        comment.crew.name.eq(crewName),
//                        comment.parent.isNull()
//                )
//                .orderBy(comment.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//    }
//
//    @Override
//    public List<CrewComment> findChildCommentsByParentIdIn(List<Long> parentIds) {
//        return jpaQueryFactory
//                .selectFrom(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .where(
//                        commentTypeEq(CREW),
//                        comment.parent.id.in(parentIds)
//                )
//                .orderBy(comment.createdAt.desc())
//                .fetch();
//    }
//
//    @Override
//    public Map<CrewComment, List<CrewComment>> findGroupedChildCommentsByParentIdIn(List<Long> parentIds) {
//        return jpaQueryFactory
//                .selectFrom(comment)
//                .innerJoin(comment.member, member).fetchJoin()
//                .innerJoin(comment.crew, crew).fetchJoin()
//                .where(
//                        commentTypeEq(CREW),
//                        comment.parent.id.in(parentIds)
//                )
//                .orderBy(comment.createdAt.desc())
//                .transform(groupBy(comment.parent).as(list(comment)));
//    }
//
//    @Override
//    public List<CrewComment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize) {
//        String sql = "SELECT * FROM (" +
//                "    SELECT " +
//                "        comment.*, " +
//                "        ROW_NUMBER() OVER (PARTITION BY comment.parent_id ORDER BY comment.created_at DESC) AS rn " +
//                "    FROM comment " +
//                "    INNER JOIN member ON comment.member_id = member.id " +
//                "    WHERE comment.type = :commentType AND comment.parent_id IN (:parentIds) " +
//                ") AS subquery " +
//                " WHERE subquery.rn <= (:childLimit)" +
//                " ORDER BY subquery.rn ASC";
//
//        List<CrewComment> resultList = entityManager.createNativeQuery(sql, CrewComment.class)
//                .setParameter("commentType", CREW.toString())
//                .setParameter("parentIds", parentIds)
//                .setParameter("childLimit", childrenSize)
//                .getResultList();
//
//        Set<Long> memberIds = resultList.stream()
//                .map(CrewComment::getId)
//                .collect(Collectors.toSet());
//
//        entityManager.createQuery("select m from Member as m where m.id in :memberIds")
//                .setParameter("memberIds", memberIds)
//                .getResultList();
//
//        return resultList;
//    }
//
//    @Override
//    public Optional<CrewComment> findCommentById(Long commentId) {
//        return Optional.ofNullable(
//                jpaQueryFactory
//                        .selectFrom(comment)
//                        .innerJoin(comment.crew, crew).fetchJoin()
//                        .where(
//                                commentTypeEq(CREW),
//                                comment.id.eq(commentId)
//                        )
//                        .fetchOne()
//        );
//    }
//
//    private BooleanExpression commentTypeEq(CommentType commentType) {
//        return commentType != null ? comment.type.eq(commentType) : null;
//    }
//}
