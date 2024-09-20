package revi1337.onsquad.crew_comment.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_comment.domain.dto.CrewCommentDomainDto;
import revi1337.onsquad.crew_comment.domain.dto.QCrewCommentDomainDto;
import revi1337.onsquad.member.dto.QSimpleMemberInfoDomainDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static revi1337.onsquad.crew_comment.domain.QCrewComment.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.member.domain.QMember.*;

@Repository
public class CrewCommentQueryDslRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    public CrewCommentQueryDslRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * 모든 댓글(부모, 자식)들을 모두 가져온다.
     * @param crewId
     * @return
     */
    public List<CrewCommentDomainDto> findCommentsWithMemberByCrewId(Long crewId) {
        return jpaQueryFactory
                .select(new QCrewCommentDomainDto(
                        crewComment.parent.id,
                        crewComment.id,
                        crewComment.content,
                        crewComment.createdAt,
                        crewComment.updatedAt,
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname
                        )
                ))
                .from(crewComment)
                .innerJoin(crewComment.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .leftJoin(crewComment.parent)
                .where(crewComment.crew.id.eq(crewId))
                .orderBy(
                        crewComment.parent.id.asc().nullsFirst(),
                        crewComment.createdAt.desc()
                )
                .fetch();
    }

    /**
     * 페이징처리에 맞게 부모 댓글들을 가져오고, id 별로 묶어서 반환한다.
     * @param crewId
     * @param pageable
     * @return
     */
    public Map<Long, CrewCommentDomainDto> findLimitedParentCommentsByCrewId(Long crewId, Pageable pageable) {
        return jpaQueryFactory
                .from(crewComment)
                .innerJoin(crewComment.crewMember, crewMember)
                .innerJoin(crewMember.member, member)
                .where(
                        crewComment.crew.id.eq(crewId),
                        crewComment.parent.isNull()
                )
                .orderBy(crewComment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(groupBy(crewComment.id)
                        .as(new QCrewCommentDomainDto(
                                crewComment.id,
                                crewComment.content,
                                crewComment.createdAt,
                                crewComment.updatedAt,
                                new QSimpleMemberInfoDomainDto(
                                        member.id,
                                        member.nickname
                                )
                        )));
    }

    /**
     * 최상위 댓글 id 들을 부모로 갖고 있는 대댓글들을 childrenSize 만큼 가져온다.
     * <p> NativeQuery 를 사용하므로, Comment 에서 Member 를 가져오려면 N + 1 가 발생한다.
     * <p> 이를 해결하기 위해 Comment 에 속한 Member id 들을 추출 후, 영속성 컨텍스트에 추가하기 위한 쿼리가 한방 더 나간다.
     * @param parentIds
     * @param childrenSize
     * @return
     */
    public List<CrewComment> findLimitedChildCommentsByParentIdIn(Collection<Long> parentIds, Integer childrenSize) {
        String sql = "SELECT * FROM (" +
                "    SELECT " +
                "        crew_comment.*, " +
                "        ROW_NUMBER() OVER (PARTITION BY crew_comment.parent_id ORDER BY crew_comment.created_at DESC) AS rn " +
                "    FROM crew_comment " +
                "    WHERE crew_comment.parent_id IN (:parentIds) " +
                ") AS subquery " +
                " WHERE subquery.rn <= (:childLimit)" +
                " ORDER BY subquery.rn ASC";

        List<CrewComment> resultList = entityManager.createNativeQuery(sql, CrewComment.class)
                .setParameter("parentIds", parentIds)
                .setParameter("childLimit", childrenSize)
                .getResultList();

        Set<Long> crewMemberIds = resultList.stream()
                .map(crewComment -> crewComment.getCrewMember().getId())
                .collect(Collectors.toSet());

        entityManager.createQuery("select cm from CrewMember as cm inner join fetch cm.member where cm.id in :crewMemberIds")
                .setParameter("crewMemberIds", crewMemberIds)
                .getResultList();

        return resultList;
    }

    public List<CrewCommentDomainDto> findChildComments(Long crewId, Long parentId, Pageable pageable) {
        return jpaQueryFactory
                .select(new QCrewCommentDomainDto(
                        crewComment.id,
                        crewComment.content,
                        crewComment.createdAt,
                        crewComment.updatedAt,
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname
                        )
                ))
                .from(crewComment)
                .innerJoin(crewComment.crewMember, crewMember)
                    .on(
                            crewComment.crew.id.eq(crewId),
                            crewComment.parent.id.eq(parentId)
                    )
                .innerJoin(crewMember.member, member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(crewComment.createdAt.desc())
                .fetch();
    }
}
