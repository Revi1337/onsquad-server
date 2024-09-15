package revi1337.onsquad.announce.domain;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.announce.domain.dto.QAnnounceInfoDomainDto;
import revi1337.onsquad.crew_member.domain.dto.QSimpleCrewMemberDomainDto;

import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.*;
import static revi1337.onsquad.announce.domain.QAnnounce.*;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.member.domain.QMember.*;

@RequiredArgsConstructor
@Repository
public class AnnounceQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<AnnounceInfoDomainDto> findAnnouncesByCrewId(Long crewId) {
        return jpaQueryFactory
                .select(new QAnnounceInfoDomainDto(
                        announce.id,
                        announce.title,
                        announce.createdAt,
                        new QSimpleCrewMemberDomainDto(
                                member.id,
                                member.nickname,
                                crewMember.role
                        )
                ))
                .from(announce)
                .innerJoin(announce.crewMember, crewMember).on(announce.crew.id.eq(crewId))
                .innerJoin(crewMember.member, member)
                .orderBy(announce.createdAt.desc())
                .fetch();
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
                        .orderBy(announce.createdAt.desc())
                        .fetchOne()
        );
    }
}
