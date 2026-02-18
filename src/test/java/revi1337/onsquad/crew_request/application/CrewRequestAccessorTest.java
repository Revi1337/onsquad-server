package revi1337.onsquad.crew_request.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;

@ExtendWith(MockitoExtension.class)
class CrewRequestAccessorTest {

    @Mock
    private CrewRequestRepository crewRequestRepository;

    @InjectMocks
    private CrewRequestAccessor crewRequestAccessor;

    @Test
    void getById() {
        Long requestId = 1L;
        CrewRequest mock = mock(CrewRequest.class);
        given(crewRequestRepository.findById(requestId)).willReturn(Optional.of(mock));

        CrewRequest request = crewRequestAccessor.getById(requestId);

        assertThat(request).isEqualTo(mock);
        verify(crewRequestRepository).findById(requestId);
    }
}
