package revi1337.onsquad.history.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HistoryTypeTest {

    @ParameterizedTest(name = "[{index}] {0} 타입 메시지 포맷팅 테스트")
    @MethodSource("provideHistoryTypeAndArgs")
    @DisplayName("각 HistoryType은 정해진 인자 개수에 맞게 포맷팅된 메시지를 생성한다")
    void formatMessage(HistoryType type, Object[] args, String expectedMessage) {
        String actualMessage = type.formatMessage(args);

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> provideHistoryTypeAndArgs() {
        return Stream.of(
                Arguments.of(HistoryType.CREW_CREATE, new Object[]{"온스쿼드"}, "[온스쿼드] 크루를 생성했습니다."),
                Arguments.of(HistoryType.CREW_REQUEST, new Object[]{"온스쿼드"}, "[온스쿼드] 크루 합류를 요청했습니다."),
                Arguments.of(HistoryType.CREW_ACCEPT, new Object[]{"온스쿼드", "태영"}, "[온스쿼드] 태영 님의 크루 합류를 수락했습니다."),
                Arguments.of(HistoryType.CREW_REJECT, new Object[]{"온스쿼드", "태영"}, "[온스쿼드] 태영 님의 크루 합류를 거절헀습니다."),
                Arguments.of(HistoryType.CREW_CANCEL, new Object[]{"온스쿼드"}, "[온스쿼드] 크루 합류 요청을 취소했습니다."),

                Arguments.of(HistoryType.SQUAD_CREATE, new Object[]{"온스쿼드", "모각코"}, "[온스쿼드 | 모각코] 스쿼드를 생성했습니다."),
                Arguments.of(HistoryType.SQUAD_REQUEST, new Object[]{"온스쿼드", "모각코"}, "[온스쿼드 | 모각코] 스쿼드 합류를 요청했습니다."),
                Arguments.of(HistoryType.SQUAD_ACCEPT, new Object[]{"온스쿼드", "모각코", "태영"}, "[온스쿼드 | 모각코] 태영 님의 스쿼드 합류를 수락했습니다."),
                Arguments.of(HistoryType.SQUAD_REJECT, new Object[]{"온스쿼드", "모각코", "태영"}, "[온스쿼드 | 모각코] 태영 님의 스쿼드 합류를 거절헀습니다."),
                Arguments.of(HistoryType.SQUAD_CANCEL, new Object[]{"온스쿼드", "모각코"}, "[온스쿼드 | 모각코] 스쿼드 합류 요청을 취소했습니다."),
                Arguments.of(HistoryType.SQUAD_COMMENT, new Object[]{"온스쿼드", "모각코"}, "[온스쿼드 | 모각코] 스쿼드에 댓글을 남겼습니다."),
                Arguments.of(HistoryType.SQUAD_COMMENT_REPLY, new Object[]{"온스쿼드", "모각코"}, "[온스쿼드 | 모각코] 스쿼드에 대댓글을 남겼습니다.")
        );
    }
}
