package revi1337.onsquad.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import revi1337.onsquad.common.util.FileNamingUtils;

class FileNamingUtilsTest {

    @Test
    @DisplayName("부모 경로가 없는 단일 파일명 입력 시 UUID파일명만 반환한다")
    void createUuidName_WithSingleFileName() {
        String input = "asdf.txt";

        String result = FileNamingUtils.createUuidName(input);

        assertThat(result).contains(".txt");
        assertThat(result.split("\\.")[0]).hasSize(36);
        assertThat(result).doesNotContain("/");
    }

    @ParameterizedTest
    @DisplayName("다양한 경로 깊이에서도 파일명만 UUID로 교체한다")
    @ValueSource(strings = {
            "root/member/profile.png",
            "a/b/c/d/test.jpg",
            "onsquad/assets/banner"
    })
    void createUuidNameWithVariousPaths(String input) {
        String result = FileNamingUtils.createUuidName(input);

        long inputSlashCount = input.chars().filter(ch -> ch == '/').count();
        long resultSlashCount = result.chars().filter(ch -> ch == '/').count();

        assertThat(resultSlashCount).isEqualTo(inputSlashCount);
        if (input.contains(".")) {
            String ext = input.substring(input.lastIndexOf("."));
            assertThat(result).endsWith(ext);
        }
    }

    @Test
    @DisplayName("공백이나 null 입력 시 순수 UUID만 반환한다")
    void createUuidNameWithInvalidInput() {
        assertThat(FileNamingUtils.createUuidName(null)).hasSize(36);
        assertThat(FileNamingUtils.createUuidName("")).hasSize(36);
    }
}
