package revi1337.onsquad.crew.domain;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;
import revi1337.onsquad.crew.dto.QCrewWithMemberAndImageDto;
import revi1337.onsquad.crew.dto.QOwnedCrewsDto;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.image.domain.QImage.*;
import static revi1337.onsquad.member.domain.QMember.*;

@RequiredArgsConstructor
public class CrewQueryRepositoryImpl implements CrewQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<CrewWithMemberAndImageDto> findCrewByName(Name name) {
        return Optional.ofNullable(
                queryForFindCrewByName()
                        .where(crew.name.eq(name))
                        .fetchOne()
        );
    }

    @Override
    public List<CrewWithMemberAndImageDto> findCrewsByName() {
        return queryForFindCrewByName()
                .fetch();
    }

    @Override
    public List<OwnedCrewsDto> findOwnedCrews(Long memberId) {
        return jpaQueryFactory
                .select(new QOwnedCrewsDto(
                        crew.name,
                        crew.detail,
                        crew.hashTags,
                        member.nickname
                ))
                .from(crew)
                .innerJoin(crew.member, member)
                .where(member.id.eq(memberId))
                .fetch();
    }

    @Override
    public Optional<Crew> findCrewByNameForUpdate(Name name) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(crew)
                        .from(crew)
                        .leftJoin(crew.member, member).fetchJoin()
                        .innerJoin(crew.image, image).fetchJoin()
                        .where(crew.name.eq(name))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Crew> findCrewWithMembersByName(Name name) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(crew)
                        .leftJoin(crew.crewMembers).fetchJoin()
                        .where(crew.name.eq(name))
                        .fetchOne()
        );
    }

    private JPAQuery<CrewWithMemberAndImageDto> queryForFindCrewByName() {
        return jpaQueryFactory
                .select(new QCrewWithMemberAndImageDto(
                        crew.name,
                        crew.detail,
                        crew.hashTags,
                        member.nickname,
                        image.imageUrl
                ))
                .from(crew)
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, member);
    }
}
