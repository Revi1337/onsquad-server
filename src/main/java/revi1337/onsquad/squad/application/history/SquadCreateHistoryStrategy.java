package revi1337.onsquad.squad.application.history;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.history.application.HistoryRecordStrategy;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.history.domain.repository.HistoryRepository;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class SquadCreateHistoryStrategy implements HistoryRecordStrategy {

    private static final String MESSAGE_FORMAT = "[%s | %s] 스쿼드를 생성했습니다.";
    private final SquadRepository squadRepository;
    private final HistoryRepository historyRepository;

    @Override
    public boolean supports(Class<?> clazz, Method method) {
        return clazz.equals(SquadCommandService.class) && method.getName().equals("newSquad");
    }

    @Override
    public void record(Object[] args, Object result) {
        Squad squad = squadRepository.findById((Long) result).orElseThrow();
        historyRepository.save(HistoryEntity.builder()
                .memberId((Long) args[0])
                .crewId(squad.getId())
                .type(HistoryType.SQUAD_CREATE)
                .message(String.format(MESSAGE_FORMAT, squad.getCrew().getName().getValue(), squad.getTitle().getValue()))
                .build());
    }
}
