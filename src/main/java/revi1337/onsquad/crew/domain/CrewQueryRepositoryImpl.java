package revi1337.onsquad.crew.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.dto.QCrewWithMemberAndImage;

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
                jpaQueryFactory
                        .select(new QCrewWithMemberAndImage(
                                crew.name,
                                crew.detail,
                                crew.hashTags,
                                member.nickname,
                                image.data
                        ))
                        .from(crew)
                        .innerJoin(crew.image, image)
                        .innerJoin(crew.member, member)
                        .where(crew.name.eq(name))
                        .fetchOne()
        );
    }

    @Override
    public List<CrewWithMemberAndImageDto> findCrewsByName() {
        return jpaQueryFactory
                .select(new QCrewWithMemberAndImage(
                        crew.name,
                        crew.detail,
                        crew.hashTags,
                        member.nickname,
                        image.data
                ))
                .from(crew)
                .innerJoin(crew.image, image)
                .innerJoin(crew.member, member)
                .fetch();
    }
}
