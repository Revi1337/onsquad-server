package revi1337.onsquad.crew_request.application.history;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_request.application.CrewRequestCommandService;
import revi1337.onsquad.history.application.HistoryRecordStrategy;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.history.domain.repository.HistoryRepository;

@Slf4j
@RequiredArgsConstructor
@Component("crewRequestCancelHistoryStrategy")
public class RequestCancelHistoryStrategy implements HistoryRecordStrategy {

    private static final String MESSAGE_FORMAT = "[%s] 크루 합류 요청을 취소했습니다.";
    private final CrewRepository crewRepository;
    private final HistoryRepository historyRepository;

    @Override
    public boolean supports(Class<?> clazz, Method method) {
        return clazz == CrewRequestCommandService.class && method.getName().equals("cancelMyRequest");
    }

    @Override
    public void record(Object[] args, Object result) {
        Crew crew = crewRepository.findById((Long) args[1]).orElseThrow();
        historyRepository.save(HistoryEntity.builder()
                .memberId((Long) args[0])
                .crewId(crew.getId())
                .type(HistoryType.CREW_CANCEL)
                .message(String.format(MESSAGE_FORMAT, crew.getName().getValue()))
                .build());
    }
}
