package revi1337.onsquad.image.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SupportAttachmentType vo 테스트")
class SupportAttachmentTypeTest {

    @Test
    @DisplayName("허용 가능한 타입을 합친 문자열을 확인한다.")
    public void convertSupportedTypeString() {
        // when
        String supportedTypeString = SupportAttachmentType.convertSupportedTypeString();

        // then
        assertThat(supportedTypeString).isEqualTo("jpeg, jpg, png, svg");
    }
}