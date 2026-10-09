package revi1337.onsquad.crew_request.application.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_request.application.CrewRequestCommandService;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.repository.HistoryRepository;

@ExtendWith(MockitoExtension.class)
class CrewRequestCancelHistoryRecorderTest {

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private CrewRequestCancelHistoryRecorder historyRecorder;

    @Test
    void supports() {
        String methodName = "cancelMyRequest";
        Method method = ReflectionUtils.findMethod(CrewRequestCommandService.class, methodName, Long.class, Long.class);

        boolean supports = historyRecorder.supports(CrewRequestCommandService.class, method);

        assertThat(supports).isTrue();
    }

    @Test
    void record() {
        Long memberId = 1L;
        Crew crew = createCrew(2L, createRevi(memberId), "우리 크루");
        Object[] args = {memberId, crew.getId()};
        Object result = 1;
        given(crewRepository.findById(crew.getId())).willReturn(Optional.of(crew));

        historyRecorder.record(args, result);

        verify(historyRepository).save(argThat(entity -> {
            assertThat(entity.getMemberId()).isEqualTo(memberId);
            assertThat(entity.getCrewId()).isEqualTo(crew.getId());
            assertThat(entity.getType()).isEqualTo(HistoryType.CREW_CANCEL);
            assertThat(entity.getMessage()).isEqualTo("[우리 크루] 크루 합류 요청을 취소했습니다.");
            return true;
        }));
    }

    @Test
    void doesNotRecord_whenNothingWasCanceled() {
        Object[] args = {1L, 2L};
        Object result = 0;

        historyRecorder.record(args, result);

        verifyNoInteractions(crewRepository, historyRepository);
    }
}
