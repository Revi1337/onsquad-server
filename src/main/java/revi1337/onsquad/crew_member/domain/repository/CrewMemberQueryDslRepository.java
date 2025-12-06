package revi1337.onsquad.crew_member.domain.repository;

import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.QCrewMemberDomainDto;
import revi1337.onsquad.member.domain.dto.QSimpleMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewMemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<CrewMemberDomainDto> fetchAllByCrewId(Long crewId, Pageable pageable) {
        List<CrewMemberDomainDto> results = jpaQueryFactory
                .select(new QCrewMemberDomainDto(
                        new QSimpleMemberDomainDto(
                                member.id,
                                member.nickname,
                                member.introduce,
                                member.mbti
                        ),
                        crewMember.requestAt
                ))
                .from(crewMember)
                .innerJoin(crewMember.member, member)
                .where(crewMember.crew.id.eq(crewId))
                .orderBy(crewMember.requestAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crewMember.id.count())
                .from(crewMember)
                .where(crewMember.crew.id.eq(crewId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
