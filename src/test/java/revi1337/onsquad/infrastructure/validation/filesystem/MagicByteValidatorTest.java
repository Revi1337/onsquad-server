package revi1337.onsquad.infrastructure.validation.filesystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import revi1337.onsquad.common.constant.SupportMediaType;
import revi1337.onsquad.common.error.FileActionException;
import revi1337.onsquad.infrastructure.validation.filesystem.error.MagicByteValidationException.UnsupportedMagicByteType;

@SuppressWarnings("deprecation")
class MagicByteValidatorTest {

    private static final byte[] JPG = bytes(0xFF, 0xD8, 0xFF);
    private static final byte[] JPEG_JFIF = bytes(0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01);
    private static final byte[] JPEG_EXIF = bytes(0xFF, 0xD8, 0xFF, 0xE1, 0x00, 0x22, 0x45, 0x78, 0x69, 0x66, 0x00, 0x00);
    private static final byte[] PNG = bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52);
    private static final byte[] SVG_XML = bytes(0x3C, 0x3F, 0x78, 0x6D, 0x6C, 0x20, 0x76, 0x65, 0x72, 0x73, 0x69, 0x6F, 0x6E, 0x3D);
    private static final byte[] SVG_TAG = bytes(0x3C, 0x73, 0x76, 0x67);
    private static final byte[] WEBP = bytes(0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x45, 0x42, 0x50);
    private static final byte[] GIF = bytes(0x47, 0x49, 0x46, 0x38, 0x37, 0x61);
    private static final byte[] WAV_RIFF = bytes(0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x41, 0x56, 0x45);
    private static final byte[] PNG_PREFIX_ONLY = bytes(0x89, 0x50, 0x4E, 0x47);

    @ParameterizedTest(name = "{0}")
    @MethodSource("supportedFiles")
    @DisplayName("허용된 파일 시그니처는 검증에 성공한다.")
    void passes_whenSignatureIsSupported(String type, byte[] binary) {
        assertThatCode(() -> MagicByteValidator.validateMagicByte(binary)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("unsupportedFiles")
    @DisplayName("허용되지 않은 파일 시그니처는 검증에 실패한다.")
    void throwsUnsupportedMagicByteType_whenSignatureIsNotSupported(String type, byte[] binary) {
        assertThatThrownBy(() -> MagicByteValidator.validateMagicByte(binary))
                .isInstanceOf(UnsupportedMagicByteType.class);
    }

    @Test
    @DisplayName("검증에 실패하면 허용된 파일 타입 목록을 메시지에 담는다.")
    void includesSupportedTypes_whenValidationFails() {
        assertThatThrownBy(() -> MagicByteValidator.validateMagicByte(GIF))
                .isInstanceOf(UnsupportedMagicByteType.class)
                .hasMessageContaining("jpg_jpeg")
                .hasMessageContaining("png")
                .hasMessageContaining("svg")
                .hasMessageContaining("webp");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("supportedFiles")
    @DisplayName("스트림으로 전달해도 허용된 파일 시그니처는 검증에 성공한다.")
    void passes_whenStreamSignatureIsSupported(String type, byte[] binary) {
        assertThatCode(() -> MagicByteValidator.validateMagicByte(new ByteArrayInputStream(binary))).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("스트림으로 전달해도 허용되지 않은 파일은 검증에 실패한다.")
    void throwsUnsupportedMagicByteType_whenStreamSignatureIsNotSupported() {
        assertThatThrownBy(() -> MagicByteValidator.validateMagicByte(new ByteArrayInputStream(GIF)))
                .isInstanceOf(UnsupportedMagicByteType.class);
    }

    @Test
    @DisplayName("스트림은 가장 긴 시그니처 길이만큼만 읽는다.")
    void readsOnlyHeader_whenStreamIsProvided() {
        byte[] large = new byte[1024 * 1024];
        System.arraycopy(PNG, 0, large, 0, PNG.length);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(large);

        MagicByteValidator.validateMagicByte(inputStream);

        assertThat(inputStream.available()).isEqualTo(large.length - SupportMediaType.MAX_SIGNATURE_LENGTH);
    }

    @Test
    @DisplayName("스트림을 읽다가 IOException 이 발생하면 파일 처리 실패 예외로 변환한다.")
    void throwsProcessFail_whenStreamReadFails() {
        InputStream broken = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };

        assertThatThrownBy(() -> MagicByteValidator.validateMagicByte(broken))
                .isInstanceOf(FileActionException.ProcessFail.class)
                .hasCauseInstanceOf(IOException.class);
    }

    private static Stream<Arguments> supportedFiles() {
        return Stream.of(
                Arguments.of("jpg", JPG),
                Arguments.of("jpeg(JFIF)", JPEG_JFIF),
                Arguments.of("jpeg(EXIF)", JPEG_EXIF),
                Arguments.of("png", PNG),
                Arguments.of("svg(xml)", SVG_XML),
                Arguments.of("svg(tag)", SVG_TAG),
                Arguments.of("webp", WEBP)
        );
    }

    private static Stream<Arguments> unsupportedFiles() {
        return Stream.of(
                Arguments.of("gif", GIF),
                Arguments.of("RIFF 컨테이너지만 webp 가 아닌 파일", WAV_RIFF),
                Arguments.of("시그니처 앞부분만 있는 png", PNG_PREFIX_ONLY),
                Arguments.of("빈 파일", new byte[0]),
                Arguments.of("일반 텍스트", "plain text".getBytes())
        );
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }
}
