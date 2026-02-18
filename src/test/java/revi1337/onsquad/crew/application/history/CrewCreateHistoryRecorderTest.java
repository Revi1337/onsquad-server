package revi1337.onsquad.crew.application.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;
import revi1337.onsquad.crew.application.CrewCommandService;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.history.domain.repository.HistoryRepository;

@ExtendWith(MockitoExtension.class)
class CrewCreateHistoryRecorderTest {

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private CrewCreateHistoryRecorder historyRecorder;

    @Test
    @DisplayName("CrewCommandService의 newCrew 메서드 호출에 대해 기록을 지원하는지 확인한다")
    void supports() {
        String methodName = "newCrew";
        Method method = ReflectionUtils.findMethod(CrewCommandService.class, methodName, Long.class, CrewCreateDto.class, String.class);

        boolean supports = historyRecorder.supports(CrewCommandService.class, method);

        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("크루 생성 성공 시, 해당 결과와 인자값을 바탕으로 생성 이력을 저장한다")
    void record() {
        Long memberId = 1L;
        Crew crew = createCrew(2L, createRevi(memberId));
        Object[] args = {memberId};
        Object result = crew.getId();
        given(crewRepository.findById(crew.getId())).willReturn(Optional.of(crew));

        historyRecorder.record(args, result);

        verify(historyRepository).save(any(HistoryEntity.class));
    }
}
