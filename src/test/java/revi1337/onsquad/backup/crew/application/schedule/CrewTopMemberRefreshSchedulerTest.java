package revi1337.onsquad.backup.crew.application.schedule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.backup.crew.config.property.CrewTopMemberProperty;
import revi1337.onsquad.backup.crew.domain.CrewTopMemberRepository;

@ContextConfiguration(classes = {CrewTopMemberRefreshScheduler.class})
@ExtendWith(SpringExtension.class)
class CrewTopMemberRefreshSchedulerTest {

    @MockBean
    private CrewTopMemberRepository crewTopMemberRepository;

    @MockBean
    private CrewTopMemberProperty crewTopMemberProperty;

    @Autowired
    private CrewTopMemberRefreshScheduler refreshScheduler;

    @Test
    @DisplayName("CrewTopMember 스케줄러를 테스트한다.")
    void success() {
        when(crewTopMemberProperty.during()).thenReturn(Duration.ofDays(7));
        when(crewTopMemberProperty.rankLimit()).thenReturn(5);

        refreshScheduler.refreshTopMembersInCrew();

        verify(crewTopMemberRepository).deleteAllInBatch();
        verify(crewTopMemberRepository).fetchAggregatedTopMembers(any(LocalDate.class), any(LocalDate.class), any());
        verify(crewTopMemberRepository).batchInsert(anyList());
    }
}