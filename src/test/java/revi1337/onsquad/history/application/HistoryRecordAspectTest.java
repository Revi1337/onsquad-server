package revi1337.onsquad.history.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import revi1337.onsquad.history.application.HistoryRecordAspectTest.TestCommandService;
import revi1337.onsquad.history.config.HistoryCandidatesInspector;

@SpringBootTest
@Import({TestCommandService.class})
class HistoryRecordAspectTest {

    @Autowired
    private TestCommandService testCommandService;

    @MockBean
    private HistoryCandidatesInspector historyCandidatesInspector;

    @MockBean
    private HistoryRecorderFactory historyRecorderFactory;

    @MockBean(name = "crewCreateHistoryRecorder")
    private HistoryRecorder historyRecorder;

    @Test
    @DisplayName("서비스 메서드 실행 후 후보 검사 및 팩토리 조회를 거쳐 히스토리가 기록된다")
    void recordHistorySuccess() {
        String testArg = "test-input";
        String testResult = "test-output";
        given(historyCandidatesInspector.canInspect(any(), anyString())).willReturn(true);
        given(historyRecorderFactory.find(any(), any())).willReturn(Optional.of(historyRecorder));

        testCommandService.successMethod(testArg);

        verify(historyRecorder, times(1)).record(any(Object[].class), eq(testResult));
    }

    @Test
    @DisplayName("리코더 실행 중 예외가 발생해도 서비스 로직은 정상 종료된다")
    void recordHistoryIgnoreException() {
        given(historyCandidatesInspector.canInspect(any(), anyString())).willReturn(true);
        given(historyRecorderFactory.find(any(), any())).willReturn(Optional.of(historyRecorder));
        doThrow(new RuntimeException("History DB Error")).when(historyRecorder).record(any(), any());

        assertThatCode(() -> testCommandService.successMethod("data"))
                .doesNotThrowAnyException();
    }

    @Service
    static class TestCommandService {

        public String successMethod(String arg) {
            return "test-output";
        }

        public void failMethod() {
            throw new RuntimeException("Original Service Error");
        }
    }
}
