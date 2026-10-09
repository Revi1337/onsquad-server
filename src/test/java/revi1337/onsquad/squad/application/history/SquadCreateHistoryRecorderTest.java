package revi1337.onsquad.squad.application.history;

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
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.model.SquadCreateSpec;
import revi1337.onsquad.squad.domain.repository.SquadRepository;

@ExtendWith(MockitoExtension.class)
class SquadCreateHistoryRecorderTest {

    @Mock
    private SquadRepository squadRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private SquadCreateHistoryRecorder historyRecorder;

    @Test
    @DisplayName("SquadCommandService의 newSquad 메서드 호출에 대해 기록을 지원하는지 확인한다")
    void supports() {
        String methodName = "newSquad";
        Method method = ReflectionUtils.findMethod(SquadCommandService.class, methodName, Long.class, Long.class, SquadCreateSpec.class);

        boolean supports = historyRecorder.supports(SquadCommandService.class, method);

        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("스쿼드 생성 성공 시, 해당 결과와 인자값을 바탕으로 생성 이력을 저장한다")
    void record() {
        Long memberId = 1L;
        Long crewId = 100L;
        Long squadId = 10L;
        Member leader = createMember(memberId, "리더");
        Crew crew = createCrew(crewId, leader, "우리 크루");
        Squad squad = createSquad(squadId, crew, leader, "새 스쿼드");
        Object[] args = {memberId, crewId, null};
        given(squadRepository.findById(squadId)).willReturn(Optional.of(squad));

        historyRecorder.record(args, squadId);

        verify(historyRepository).save(argThat(entity -> {
            assertThat(entity.getMemberId()).isEqualTo(memberId);
            assertThat(entity.getCrewId()).isEqualTo(crewId);
            assertThat(entity.getSquadId()).isEqualTo(squadId);
            assertThat(entity.getType()).isEqualTo(HistoryType.SQUAD_CREATE);
            assertThat(entity.getMessage()).isEqualTo("[우리 크루 | 새 스쿼드] 스쿼드를 생성했습니다.");
            return true;
        }));
    }
}
