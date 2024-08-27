package revi1337.onsquad.crew.domain;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.dto.QCrewWithMemberAndImageDto;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew.domain.QCrew.crew;
import static revi1337.onsquad.image.domain.QImage.image;
import static revi1337.onsquad.member.domain.QMember.member;

@RequiredArgsConstructor
@Repository
public class CrewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<CrewWithMemberAndImageDto> findCrewByName(Name name) {
        return Optional.ofNullable(
                queryForFindCrewByName()
                        .where(crew.name.eq(name))
                        .fetchOne()
        );
    }

    public List<CrewWithMemberAndImageDto> findCrewsByName() {
        return queryForFindCrewByName()
                .fetch();
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
