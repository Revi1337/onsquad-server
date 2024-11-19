package revi1337.onsquad.announce.domain;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static revi1337.onsquad.announce.domain.QAnnounce.announce;
import static revi1337.onsquad.crew_member.domain.QCrewMember.crewMember;
import static revi1337.onsquad.member.domain.QMember.member;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.announce.domain.dto.QAnnounceInfoDomainDto;
import revi1337.onsquad.crew_member.domain.dto.QSimpleCrewMemberDomainDto;

@RequiredArgsConstructor
@Repository
public class AnnounceQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<AnnounceInfoDomainDto> findAnnouncesByCrewId(Long crewId, Long limit) {
        JPAQuery<AnnounceInfoDomainDto> query = jpaQueryFactory
                .select(new QAnnounceInfoDomainDto(
                        announce.id,
                        announce.title,
                        announce.createdAt,
                        announce.fixed,
                        announce.fixedAt,
                        new QSimpleCrewMemberDomainDto(
                                member.id,
                                member.nickname,
                                crewMember.role
                        )
                ))
                .from(announce)
                .innerJoin(announce.crewMember, crewMember).on(announce.crew.id.eq(crewId))
                .innerJoin(crewMember.member, member)
                .orderBy(
                        announce.fixed.desc(),
                        announce.fixedAt.asc(),
                        announce.createdAt.desc()
                );

        if (limit != null) {
            return query.limit(limit).fetch();
        }

        return query.fetch();
    }

    public Optional<AnnounceInfoDomainDto> findAnnounceByCrewIdAndId(Long crewId, Long id, Long memberId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(new QAnnounceInfoDomainDto(
                                new CaseBuilder()
                                        .when(member.id.eq(memberId))
                                        .then(TRUE)
                                        .otherwise(FALSE),
                                announce.id,
                                announce.title,
                                announce.content,
                                announce.createdAt,
                                announce.fixed,
                                announce.fixedAt,
                                new QSimpleCrewMemberDomainDto(
                                        member.id,
                                        member.nickname,
                                        crewMember.role
                                )
                        ))
                        .from(announce)
                        .innerJoin(announce.crewMember, crewMember)
                        .on(
                                announce.crew.id.eq(crewId),
                                announce.id.eq(id)
                        )
                        .innerJoin(crewMember.member, member)
                        .fetchOne()
        );
    }
}
