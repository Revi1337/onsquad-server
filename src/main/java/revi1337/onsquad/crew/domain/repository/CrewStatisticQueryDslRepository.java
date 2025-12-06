package revi1337.onsquad.crew.domain.repository;

import static com.querydsl.jpa.JPAExpressions.select;
import static revi1337.onsquad.crew_hashtag.domain.entity.QCrewHashtag.crewHashtag;
import static revi1337.onsquad.crew_member.domain.entity.QCrewMember.crewMember;
import static revi1337.onsquad.crew_participant.domain.entity.QCrewParticipant.crewParticipant;
import static revi1337.onsquad.squad.domain.entity.QSquad.squad;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.CrewStatisticDomainDto;
import revi1337.onsquad.crew.domain.dto.QCrewStatisticDomainDto;

@RequiredArgsConstructor
@Repository
public class CrewStatisticQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * select 절 서브쿼리만으로도 쿼리가 가능합니다. from 절은 생략 불가하기 떄문에 데이터가 가장 적을거라고 예상되는 Table 을 Dummy Table 로 사용합니다.
     */
    public CrewStatisticDomainDto getStatisticById(Long crewId) { // TODO Count 쿼리 분리 필요.
        return jpaQueryFactory
                .select(new QCrewStatisticDomainDto(
                        select(crewParticipant.id.count())
                                .from(crewParticipant)
                                .where(crewParticipant.crew.id.eq(crewId)),
                        select(squad.id.count())
                                .from(squad)
                                .where(squad.crew.id.eq(crewId)),
                        select(crewMember.id.count())
                                .from(crewMember)
                                .where(crewMember.crew.id.eq(crewId))
                ))
                .from(crewHashtag) // Use Dummy Table. Cannot Omit From Operation
                .fetchFirst();
    }
}
