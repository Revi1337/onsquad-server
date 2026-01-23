package revi1337.onsquad.crew.application.history;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.application.CrewCommandService;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.history.application.HistoryRecordStrategy;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.history.domain.repository.HistoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewCreateHistoryStrategy implements HistoryRecordStrategy {

    private static final String MESSAGE_FORMAT = "[%s] 크루를 생성했습니다.";
    private final CrewRepository crewRepository;
    private final HistoryRepository historyRepository;

    @Override
    public boolean supports(Class<?> clazz, Method method) {
        return clazz.equals(CrewCommandService.class) && method.getName().equals("newCrew");
    }

    @Override
    public void record(Object[] args, Object result) {
        Crew crew = crewRepository.findById((Long) result).orElseThrow();
        historyRepository.save(HistoryEntity.builder()
                .memberId((Long) args[0])
                .crewId(crew.getId())
                .type(HistoryType.CREW_CREATE)
                .message(String.format(MESSAGE_FORMAT, crew.getName().getValue()))
                .build());
    }
}
