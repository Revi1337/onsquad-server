package revi1337.onsquad.crew.domain;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew.domain.dto.QCrewInfoDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.dto.QSimpleMemberInfoDomainDto;

import java.util.List;
import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.*;
import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.crew_member.domain.QCrewMember.*;
import static revi1337.onsquad.image.domain.QImage.image;
import static revi1337.onsquad.member.domain.QMember.member;

@RequiredArgsConstructor
@Repository
public class CrewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CrewInfoDomainDto> findCrewByName(Name name) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(new QCrewInfoDomainDto(
                                crew.id,
                                crew.name,
                                crew.introduce,
                                crew.detail,
                                image.imageUrl,
                                crew.kakaoLink,
                                crew.hashTags,
                                select(crewMember.count())
                                        .from(crewMember)
                                        .where(crewMember.crew.id.eq(crew.id)),
                                new QSimpleMemberInfoDomainDto(
                                        member.id,
                                        member.nickname
                                )
                        ))
                        .from(crew)
                        .innerJoin(crew.image, image).on(crew.name.eq(name))
                        .innerJoin(crew.member, member)
                        .fetchOne()
        );
    }

    public Page<CrewInfoDomainDto> findCrewsByName(String name, Pageable pageable) {
        List<CrewInfoDomainDto> fetchedCrews = jpaQueryFactory
                .select(new QCrewInfoDomainDto(
                        crew.id,
                        crew.name,
                        crew.introduce,
                        image.imageUrl,
                        crew.kakaoLink,
                        crew.hashTags,
                        select(crewMember.count())
                                .from(crewMember)
                                .from(crewMember)
                                .where(crewMember.crew.id.eq(crew.id)),
                        new QSimpleMemberInfoDomainDto(
                                member.id,
                                member.nickname
                        )
                ))
                .from(crew)
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, member)
                .where(crew.name.value.startsWithIgnoreCase(name))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(crew.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(crew.count())
                .from(crew)
                .where(crew.name.value.startsWithIgnoreCase(name));

        return PageableExecutionUtils.getPage(fetchedCrews, pageable, countQuery::fetchOne);
    }
}
