package revi1337.onsquad.infrastructure.file.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.common.application.file.UUIDFilenameConverter;

class UUIDFilenameConverterTest {

    private final UUIDFilenameConverter uuidFilenameConverter = new UUIDFilenameConverter();

    @Test
    @DisplayName("UUID 파일 이름 변경에 성공한다.")
    void success() {
        String testFileName = "test.png";

        String convertFileName = uuidFilenameConverter.convert(testFileName);

        assertThat(convertFileName).isNotEqualTo(testFileName);
    }
}
