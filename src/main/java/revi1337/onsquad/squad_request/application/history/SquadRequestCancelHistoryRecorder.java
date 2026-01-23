package revi1337.onsquad.squad_request.application.history;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.history.application.HistoryRecorder;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.history.domain.repository.HistoryRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_request.application.SquadRequestCommandService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SquadRequestCancelHistoryRecorder implements HistoryRecorder {

    private static final String MESSAGE_FORMAT = "[%s | %s] 스쿼드 합류 요청을 취소했습니다.";
    private final SquadRepository squadRepository;
    private final HistoryRepository historyRepository;

    @Override
    public boolean supports(Class<?> clazz, Method method) {
        return clazz.equals(SquadRequestCommandService.class) && method.getName().equals("cancelMyRequest");
    }

    @Override
    public void record(Object[] args, Object result) {
        Squad squad = squadRepository.findWithCrewById((Long) args[1]).orElseThrow();
        historyRepository.save(HistoryEntity.builder()
                .memberId((Long) args[0])
                .crewId(squad.getId())
                .type(HistoryType.SQUAD_CANCEL)
                .message(String.format(MESSAGE_FORMAT, squad.getCrew().getName().getValue(), squad.getTitle().getValue()))
                .build());
    }
}
