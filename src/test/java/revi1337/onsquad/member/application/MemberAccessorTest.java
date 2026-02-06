package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.member.error.MemberBusinessException;

@ExtendWith(MockitoExtension.class)
class MemberAccessorTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberAccessor memberAccessor;

    @Test
    void getById() {
        Long invalidId = 1L;
        given(memberRepository.findById(invalidId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberAccessor.getById(invalidId))
                .isInstanceOf(MemberBusinessException.NotFound.class);
    }
}
