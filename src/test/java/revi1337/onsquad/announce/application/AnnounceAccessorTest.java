package revi1337.onsquad.announce.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.error.AnnounceBusinessException;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;

@ExtendWith(MockitoExtension.class)
class AnnounceAccessorTest {

    @Mock
    private AnnounceRepository announceRepository;

    @InjectMocks
    private AnnounceAccessor announceAccessor;

    @Test
    void getById() {
        Long announceId = 1L;
        Announce mock = mock(Announce.class);
        given(announceRepository.findById(announceId)).willReturn(Optional.of(mock));

        Announce announce = announceAccessor.getById(announceId);

        assertThat(announce).isEqualTo(mock);
        verify(announceRepository).findById(announceId);
    }

    @Test
    void getById2() {
        Long announceId = 1L;
        given(announceRepository.findById(announceId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> announceAccessor.getById(announceId))
                .isExactlyInstanceOf(AnnounceBusinessException.NotFound.class);
    }
}
