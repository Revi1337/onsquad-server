package revi1337.onsquad.squad_member.domain.entity.vo;

import java.util.EnumSet;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JoinStatus {

    PENDING("보류"), ACCEPT("수락"), REJECT("거절");

    private final String text;

    public static boolean checkEquivalence(JoinStatus status, String constant) {
        return status.getText().equals(constant);
    }

    public static EnumSet<JoinStatus> defaultEnumSet() {
        return EnumSet.allOf(JoinStatus.class);
    }

    public static String convertSupportedTypeString() {
        return defaultEnumSet().stream()
                .map(JoinStatus::getText)
                .collect(Collectors.joining(", "));
    }
}
