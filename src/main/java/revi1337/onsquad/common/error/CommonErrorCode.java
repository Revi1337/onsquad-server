package revi1337.onsquad.common.error;

import java.util.EnumSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_INPUT_VALUE(400, "C001", "유효성 검증 실패"),
    MISSING_PARAMETER(400, "C002", "파라미터가 필요한 요청"),
    METHOD_NOT_SUPPORT(405, "C003", "지원하지 않는 메서드"),
    PARAMETER_TYPE_MISMATCH(400, "C004", "파라미터 타입 불일치"),
    NOT_FOUND(404, "C005", "존재하지 않는 API 요청"),
    INTERNAL_SERVER_ERROR(500, "C006", "서버에서 처리 불가한 요청"),
    TO_MANY_REQUEST(429, "C007", "요청 한도가 초과되었습니다. 잠시 후 이용해 주세요."),
    ALREADY_REQUEST(409, "C008", "이미 처리된 요청입니다. uri : %s"),
    MAINTENANCE_TIME(503, "C009", "시스템 점검 시간입니다."),
    UNSUPPORTED_MEDIA_TYPE(415, "C010", "지원하지 않는 요청 형식"),
    PAYLOAD_TOO_LARGE(413, "C011", "요청 크기가 허용 범위를 초과했습니다."),
    NOT_ACCEPTABLE(406, "C012", "지원하지 않는 응답 형식");

    private final int status;
    private final String code;
    private final String description;

    public static CommonErrorCode fromStatus(int status) {
        return switch (status) {
            case 404 -> NOT_FOUND;
            case 405 -> METHOD_NOT_SUPPORT;
            case 406 -> NOT_ACCEPTABLE;
            case 413 -> PAYLOAD_TOO_LARGE;
            case 415 -> UNSUPPORTED_MEDIA_TYPE;
            default -> status < 500 ? INVALID_INPUT_VALUE : INTERNAL_SERVER_ERROR;
        };
    }

    public static EnumSet<CommonErrorCode> defaultEnumSet() {
        return EnumSet.allOf(CommonErrorCode.class);
    }

    public static EnumSet<CommonErrorCode> forCommonCase() {
        EnumSet<CommonErrorCode> commonCase = EnumSet.noneOf(CommonErrorCode.class);
        defaultEnumSet().stream()
                .filter(errorCode -> errorCode.getCode().startsWith("C"))
                .forEach(commonCase::add);
        return commonCase;
    }
}
