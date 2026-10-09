package revi1337.onsquad.common.constant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
class SupportMediaTypeTest {

    @Test
    @DisplayName("와일드카드 구간의 값이 달라도 JPEG(EXIF) 시그니처와 일치한다.")
    void matchesJpegExif_whenWildcardBytesDiffer() {
        byte[] binary = bytes(0xFF, 0xD8, 0xFF, 0xE1, 0x12, 0x34, 0x45, 0x78, 0x69, 0x66, 0x00, 0x00);

        assertThat(SupportMediaType.JPEG_EXIF.matches(binary)).isTrue();
    }

    @Test
    @DisplayName("와일드카드 구간이 아닌 바이트가 다르면 JPEG(EXIF) 시그니처와 일치하지 않는다.")
    void doesNotMatchJpegExif_whenFixedByteDiffers() {
        byte[] binary = bytes(0xFF, 0xD8, 0xFF, 0xE1, 0x12, 0x34, 0x45, 0x78, 0x69, 0x66, 0x00, 0x01);

        assertThat(SupportMediaType.JPEG_EXIF.matches(binary)).isFalse();
    }

    @Test
    @DisplayName("와일드카드 구간이 여러 개여도 모두 건너뛰고 나머지를 비교한다.")
    void matchesWebp_whenAnyFileSize() {
        byte[] binary = bytes(0x52, 0x49, 0x46, 0x46, 0xAB, 0xCD, 0xEF, 0x01, 0x57, 0x45, 0x42, 0x50);

        assertThat(SupportMediaType.WEBP.matches(binary)).isTrue();
    }

    @Test
    @DisplayName("시그니처보다 짧은 데이터는 일치하지 않는다.")
    void doesNotMatch_whenBinaryIsShorterThanSignature() {
        assertThat(SupportMediaType.PNG.matches(bytes(0x89, 0x50, 0x4E))).isFalse();
    }

    @Test
    @DisplayName("0x80 이상의 바이트도 부호 없이 비교한다.")
    void comparesBytesAsUnsigned() {
        assertThat(SupportMediaType.PNG.matches(bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A))).isTrue();
    }

    @Test
    @DisplayName("가장 긴 시그니처 길이는 14바이트이다.")
    void maxSignatureLength() {
        assertThat(SupportMediaType.MAX_SIGNATURE_LENGTH).isEqualTo(14);
    }

    @Test
    @DisplayName("허용된 파일 타입 목록 문자열은 모든 타입을 소문자로 나열한다.")
    void convertSupportedTypeString() {
        assertThat(SupportMediaType.convertSupportedTypeString())
                .isEqualTo("jpg_jpeg, jpeg_jfif, jpeg_exif, png, svg, svg2, webp");
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }
}
