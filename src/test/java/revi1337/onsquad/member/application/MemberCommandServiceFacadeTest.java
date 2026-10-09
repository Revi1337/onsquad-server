package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.domain.error.MemberBusinessException;
import revi1337.onsquad.member.infrastructure.MemberFileStorageManager;

@ExtendWith(MockitoExtension.class)
class MemberCommandServiceFacadeTest {

    @Mock
    private MemberCommandService memberCommandService;

    @Mock
    private MemberFileStorageManager memberFileStorageManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MemberCommandServiceFacade facade;

    @Test
    @DisplayName("이미지 업데이트 중 실패하면 업로드된 파일을 삭제하는 이벤트를 발행한다.")
    void updateImage() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "data".getBytes());
        String uploadedUrl = "https://s3.url/test.png";
        given(memberFileStorageManager.upload(any())).willReturn(uploadedUrl);
        willThrow(MemberBusinessException.NotFound.class)
                .given(memberCommandService).updateImage(anyLong(), anyString());

        assertThatThrownBy(() -> facade.updateImage(1L, file))
                .isInstanceOf(MemberBusinessException.class);
        verify(eventPublisher).publishEvent(any(FileDeleteEvent.class));
    }

    @Test
    @DisplayName("이미지 업데이트 중 DB 예외가 발생해도 업로드된 파일을 삭제하는 이벤트를 발행한다.")
    void publishesFileDeleteEvent_whenUnexpectedExceptionOccurs() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "data".getBytes());
        String uploadedUrl = "https://s3.url/test.png";
        given(memberFileStorageManager.upload(any())).willReturn(uploadedUrl);
        willThrow(new DataIntegrityViolationException("conflict"))
                .given(memberCommandService).updateImage(anyLong(), anyString());

        assertThatThrownBy(() -> facade.updateImage(1L, file))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(eventPublisher).publishEvent(any(FileDeleteEvent.class));
    }
}
