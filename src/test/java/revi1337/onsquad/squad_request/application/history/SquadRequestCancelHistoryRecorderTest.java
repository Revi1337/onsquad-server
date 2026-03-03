package revi1337.onsquad.squad_request.application.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.repository.HistoryRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_request.application.SquadRequestCommandService;

@ExtendWith(MockitoExtension.class)
class SquadRequestCancelHistoryRecorderTest {

    @Mock
    private SquadRepository squadRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private SquadRequestCancelHistoryRecorder historyRecorder;

    @Test
    @DisplayName("SquadRequestCommandService의 cancelMyRequest 메서드 호출에 대해 기록을 지원한다")
    void supports() {
        String methodName = "cancelMyRequest";
        Method method = ReflectionUtils.findMethod(SquadRequestCommandService.class, methodName, Long.class, Long.class);

        boolean supports = historyRecorder.supports(SquadRequestCommandService.class, method);

        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("스쿼드 신청 취소 시, 해당 인자값들을 바탕으로 취소 이력을 정상적으로 저장한다")
    void record() {
        Long memberId = 1L;
        Long crewId = 100L;
        Long squadId = 10L;
        Member leader = createMember(memberId, "리더");
        Crew crew = createCrew(crewId, leader, "우리 크루");
        Squad squad = createSquad(squadId, crew, leader, "취소할 스쿼드");
        Object[] args = {memberId, squadId};
        Object result = 1;

        given(squadRepository.findWithCrewById(squadId)).willReturn(Optional.of(squad));

        historyRecorder.record(args, result);

        verify(historyRepository).save(argThat(entity -> {
            assertThat(entity.getMemberId()).isEqualTo(memberId);
            assertThat(entity.getCrewId()).isEqualTo(squad.getCrew().getId());
            assertThat(entity.getSquadId()).isEqualTo(squadId);
            assertThat(entity.getType()).isEqualTo(HistoryType.SQUAD_CANCEL);
            assertThat(entity.getMessage()).isEqualTo("[우리 크루 | 취소할 스쿼드] 스쿼드 합류 요청을 취소했습니다.");
            return true;
        }));
    }
}
