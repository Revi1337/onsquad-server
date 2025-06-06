package revi1337.onsquad.inrastructure.file.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlUtilsTest {

    @Test
    @DisplayName("Path 정보들 추출에 성공한다.")
    void extractPathExcludeFirstSlash() {
        String baseDomain = "https://basedomain.com";
        List<String> urls = IntStream.rangeClosed(1, 100)
                .mapToObj(sequence -> String.format("%s/%s", baseDomain, sequence))
                .toList();

        List<String> strings = UrlUtils.extractPathExcludeFirstSlash(baseDomain, urls);

        assertThat(strings).isEqualTo(IntStream.rangeClosed(1, 100).mapToObj(String::valueOf).toList());
    }
}
