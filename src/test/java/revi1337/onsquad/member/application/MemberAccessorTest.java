package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.member.domain.error.MemberBusinessException;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberAccessorTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberAccessor memberAccessor;

    @Test
    @DisplayName("존재하지 않는 ID 로 회원을 조회하면 MemberBusinessException.NotFound 예외가 발생한다.")
    void getById() {
        Long invalidId = 1L;
        given(memberRepository.findById(invalidId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberAccessor.getById(invalidId))
                .isInstanceOf(MemberBusinessException.NotFound.class);
    }
}
