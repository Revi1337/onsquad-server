package revi1337.onsquad.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NotificationDetailTest {

    @ParameterizedTest(name = "[{index}] {0} 타입 메시지 포맷팅 테스트")
    @MethodSource("provideNotificationDetailAndArgs")
    @DisplayName("각 NotificationDetail은 정해진 인자 개수에 맞게 포맷팅된 메시지를 생성한다")
    void formatMessage(NotificationDetail detail, Object[] args, String expectedMessage) {
        String actualMessage = detail.formatMessage(args);

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> provideNotificationDetailAndArgs() {
        return Stream.of(
                Arguments.of(NotificationDetail.CONNECT, null, ""),
                Arguments.of(NotificationDetail.HEARTBEAT, new Object[]{"ignore"}, ""),

                Arguments.of(NotificationDetail.CREW_ACCEPT, null, "크루에 합류하였습니다. 지금 활동을 시작해보세요!"),
                Arguments.of(NotificationDetail.CREW_REJECT, new Object[]{}, "크루 합류가 거절되었습니다."),
                Arguments.of(NotificationDetail.SQUAD_ACCEPT, null, "스쿼드에 합류하였습니다. 지금 활동을 시작해보세요!"),
                Arguments.of(NotificationDetail.SQUAD_REJECT, null, "스쿼드 합류가 거절되었습니다."),

                Arguments.of(NotificationDetail.CREW_REQUEST, new Object[]{"revi"}, "revi 님이 크루 합류를 요청하였습니다."),
                Arguments.of(NotificationDetail.SQUAD_REQUEST, new Object[]{"revi"}, "revi 님이 스쿼드 합류를 요청하였습니다."),
                Arguments.of(NotificationDetail.COMMENT, new Object[]{"revi"}, "revi 님이 스쿼드에 댓글을 남겼습니다."),
                Arguments.of(NotificationDetail.COMMENT_REPLY, new Object[]{"revi"}, "revi 님이 대댓글을 남겼습니다.")
        );
    }
}
