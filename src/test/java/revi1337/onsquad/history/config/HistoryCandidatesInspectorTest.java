package revi1337.onsquad.history.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ReflectionUtils;
import revi1337.onsquad.crew.application.CrewCommandService;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;

@ContextConfiguration(classes = HistoryCandidatesInspector.class)
@ExtendWith(SpringExtension.class)
class HistoryCandidatesInspectorTest {

    @Autowired
    private HistoryCandidatesInspector historyCandidatesInspector;

    @Test
    @DisplayName("히스토리 기록 후보(Candidate) 목록에 등록된 서비스 메서드인 경우 true를 반환한다")
    void canInspect() {
        Class<CrewCommandService> clazz = CrewCommandService.class;
        Method method = ReflectionUtils.findMethod(clazz, "newCrew", Long.class, CrewCreateDto.class, String.class);

        boolean canInspect = historyCandidatesInspector.canInspect(clazz, method.getName());

        assertThat(canInspect).isTrue();
    }

    @Test
    @DisplayName("후보 목록에 존재하지 않는 메서드인 경우 히스토리 기록 대상이 아닌 것으로 판단하여 false를 반환한다")
    void canInspectFail() {
        Class<CrewCommandService> clazz = CrewCommandService.class;
        Method method = ReflectionUtils.findMethod(clazz, "deleteImage", Long.class, Long.class);

        boolean canInspect = historyCandidatesInspector.canInspect(clazz, method.getName());

        assertThat(canInspect).isFalse();
    }
}
