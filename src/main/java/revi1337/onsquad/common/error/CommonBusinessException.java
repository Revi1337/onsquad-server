package revi1337.onsquad.common.error;

import lombok.Getter;

@Getter
public abstract class CommonBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public CommonBusinessException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class ToManyRequest extends CommonBusinessException {

        public ToManyRequest(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class MaintenanceTime extends CommonBusinessException {

        public MaintenanceTime(ErrorCode errorCode, String from, String to) {
            super(errorCode, String.format("%s (%s ~ %s)", errorCode.getDescription(), from, to));
        }
    }
}
