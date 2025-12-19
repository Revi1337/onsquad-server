package revi1337.onsquad.history.domain.repository;

import static revi1337.onsquad.history.domain.entity.QHistoryEntity.historyEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@RequiredArgsConstructor
@Repository
public class HistoryQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<HistoryEntity> findHistoriesByMemberIdAndRecordedAtBetween(Long memberId, LocalDateTime from, LocalDateTime to, HistoryType type) {
        return jpaQueryFactory
                .selectFrom(historyEntity)
                .where(
                        historyEntity.memberId.eq(memberId),
                        historyEntity.recordedAt.between(from, to),
                        eqType(type)
                )
                .orderBy(historyEntity.recordedAt.desc())
                .fetch();
    }

    private BooleanExpression eqType(HistoryType type) {
        if (type == null) {
            return null;
        }
        return historyEntity.type.eq(type);
    }
}
