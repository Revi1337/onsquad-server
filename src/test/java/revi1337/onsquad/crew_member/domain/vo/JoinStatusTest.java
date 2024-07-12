package revi1337.onsquad.crew_member.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JoinStatus 테스트")
class JoinStatusTest {
    
    @Test
    @DisplayName("크루 참여상태 종류를 확인한다.")
    public void convertSupportedTypeString() {
        // given
        String convertedString = JoinStatus.convertSupportedTypeString();

        // when & then
        assertThat(convertedString).isEqualTo("보류, 수락, 거절");
    }
}