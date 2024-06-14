package revi1337.onsquad.image.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("대표 이미지 Entity 테스트")
class ImageTest {

    private DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    @Test
    @DisplayName("대표 이미지의 Magic Byte 가 png, jpg, jpeg 중 하나이면 성공한다. 1")
    public void imageTest() throws IOException {
        Resource resource = defaultResourceLoader.getResource("classpath:" + "images/revi1337.png");
        byte[] binaryData = FileCopyUtils.copyToByteArray(resource.getInputStream());

        assertThat(new Image(binaryData)).isNotNull();
    }

    @Test
    @DisplayName("대표 이미지의 Magic Byte 가 png, jpg, jpeg 중 하나이면 성공한다. 2")
    public void imageTest2() throws IOException {
        Resource resource = defaultResourceLoader.getResource("classpath:" + "images/cat.jpg");
        byte[] binaryData = FileCopyUtils.copyToByteArray(resource.getInputStream());

        assertThat(new Image(binaryData)).isNotNull();
    }

    @Test
    @DisplayName("대표 이미지의 Magic Byte 가 png, jpg, jpeg 가 아니면 실패한다.")
    public void imageTest3() throws IOException {
        Resource resource = defaultResourceLoader.getResource("classpath:" + "images/cat.gif");
        byte[] binaryData = FileCopyUtils.copyToByteArray(resource.getInputStream());

        assertThatThrownBy(() -> new Image(binaryData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("대표이미지는 jpg, jpeg, png 만 가능합니다.");
    }
}