package revi1337.onsquad.image.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.image.domain.vo.SupportAttachmentType;
import revi1337.onsquad.image.error.exception.AttachmentValidationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AttachmentMagicByteValidator 테스트")
class AttachmentMagicByteValidatorTest {
    
    @Test
    @DisplayName("jpeg 파일은 검증에 성공한다.")
    public void validateMagicByte() {
        // given
        byte[] magicByte = SupportAttachmentType.JPEG.getMagicByte();

        // when & then
        AttachmentMagicByteValidator.validateMagicByte(magicByte);
    }

    @Test
    @DisplayName("jpg 파일은 검증에 성공한다.")
    public void validateMagicByte2() {
        // given
        byte[] magicByte = SupportAttachmentType.JPG.getMagicByte();

        // when & then
        AttachmentMagicByteValidator.validateMagicByte(magicByte);
    }

    @Test
    @DisplayName("svg 파일은 검증에 성공한다.")
    public void validateMagicByte3() {
        // given
        byte[] magicByte = SupportAttachmentType.SVG.getMagicByte();

        // when & then
        AttachmentMagicByteValidator.validateMagicByte(magicByte);
    }

    @Test
    @DisplayName("png 파일은 검증에 성공한다.")
    public void validateMagicByte4() {
        // given
        byte[] magicByte = SupportAttachmentType.PNG.getMagicByte();

        // when & then
        AttachmentMagicByteValidator.validateMagicByte(magicByte);
    }

    @Test
    @DisplayName("gif 파일은 검증에 실패한다.")
    public void validateMagicByte5() {
        // given
        byte[] magicByte = new byte[]{(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61, (byte) 0xd0};

        // when & then
        assertThatThrownBy(() -> AttachmentMagicByteValidator.validateMagicByte(magicByte))
                .isExactlyInstanceOf(AttachmentValidationException.UnsupportedAttachmentType.class)
                .hasMessage("이미지 업로드는 jpeg, jpg, png, svg 만 가능합니다.");
    }

}