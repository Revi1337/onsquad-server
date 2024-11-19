package revi1337.onsquad.image.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import revi1337.onsquad.image.error.exception.AttachmentValidationException;

@DisplayName("AttachmentMagicByteValidator 테스트")
class AttachmentMagicByteValidatorTest {

    private DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    @Test
    @DisplayName("jpeg 와 jpg 파일은 검증에 성공한다.")
    public void validateMagicByte() throws IOException {
        // given
        Resource resource = defaultResourceLoader.getResource("classpath:" + "images/cat.jpg");
        byte[] binaryData = FileCopyUtils.copyToByteArray(resource.getInputStream());

        // when & then
        AttachmentMagicByteValidator.validateMagicByte(binaryData);
    }

    @Test
    @DisplayName("png 파일은 검증에 성공한다.")
    public void validateMagicByte2() throws IOException {
        // given
        Resource resource = defaultResourceLoader.getResource("classpath:" + "images/revi1337.png");
        byte[] binaryData = FileCopyUtils.copyToByteArray(resource.getInputStream());

        // when & then
        AttachmentMagicByteValidator.validateMagicByte(binaryData);
    }

    @Test
    @DisplayName("svg 파일은 검증에 성공한다.")
    public void validateMagicByte3() throws IOException {
        // given
        Resource resource = defaultResourceLoader.getResource("classpath:" + "images/dragon.svg");
        byte[] binaryData = FileCopyUtils.copyToByteArray(resource.getInputStream());

        // when & then
        AttachmentMagicByteValidator.validateMagicByte(binaryData);
    }

    @Test
    @DisplayName("gif 파일은 검증에 실패한다.")
    public void validateMagicByte4() throws IOException {
        // given
        Resource resource = defaultResourceLoader.getResource("classpath:" + "images/cat.gif");
        byte[] binaryData = FileCopyUtils.copyToByteArray(resource.getInputStream());

        // when & then
        assertThatThrownBy(() -> AttachmentMagicByteValidator.validateMagicByte(binaryData))
                .isExactlyInstanceOf(AttachmentValidationException.UnsupportedAttachmentType.class)
                .hasMessage("이미지 업로드는 jpeg, jpg, png, svg 만 가능합니다.");
    }
}