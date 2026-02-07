package revi1337.onsquad.history.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.application.CrewCommandService;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;

class HistoryRecorderFactoryTest extends ApplicationLayerTestSupport {

    @Autowired
    private HistoryRecorderFactory historyRecorderFactory;

    @Test
    @DisplayName("지원 대상(Class, Method)이 주어지면, 등록된 전략 목록에서 일치하는 HistoryRecorder를 반환한다")
    void findSuccess() {
        Class<CrewCommandService> clazz = CrewCommandService.class;
        Method method = ReflectionUtils.findMethod(clazz, "newCrew", Long.class, CrewCreateDto.class, String.class);

        Optional<HistoryRecorder> historyRecorder = historyRecorderFactory.find(clazz, method);

        assertThat(historyRecorder).isPresent();
    }

    @Test
    @DisplayName("히스토리 기록 대상이 아닌 메서드가 주어지면, 빈 Optional을 반환한다")
    void find() {
        Class<CrewCommandService> clazz = CrewCommandService.class;
        Method method = ReflectionUtils.findMethod(clazz, "deleteImage", Long.class, Long.class);

        Optional<HistoryRecorder> historyRecorder = historyRecorderFactory.find(clazz, method);

        assertThat(historyRecorder).isEmpty();
    }
}
