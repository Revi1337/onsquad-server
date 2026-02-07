package revi1337.onsquad.history.domain.repository;

import static revi1337.onsquad.history.domain.entity.QHistoryEntity.historyEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@Repository
@RequiredArgsConstructor
public class HistoryQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<HistoryEntity> findAllByMemberIdAndDateRange(Long memberId, LocalDate from, LocalDate to, @Nullable HistoryType type, Pageable pageable) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

        List<HistoryEntity> histories = jpaQueryFactory
                .selectFrom(historyEntity)
                .where(
                        historyEntity.memberId.eq(memberId),
                        historyEntity.recordedAt.goe(fromDateTime),
                        historyEntity.recordedAt.lt(toDateTime),
                        historyTypeEq(type)
                )
                .orderBy(historyEntity.recordedAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(historyEntity.id.count())
                .from(historyEntity)
                .where(
                        historyEntity.memberId.eq(memberId),
                        historyEntity.recordedAt.goe(fromDateTime),
                        historyEntity.recordedAt.lt(toDateTime),
                        historyTypeEq(type)
                );

        return PageableExecutionUtils.getPage(histories, pageable, countQuery::fetchOne);
    }

    private BooleanExpression historyTypeEq(HistoryType type) {
        if (type == null) {
            return null;
        }
        return historyEntity.type.eq(type);
    }
}
