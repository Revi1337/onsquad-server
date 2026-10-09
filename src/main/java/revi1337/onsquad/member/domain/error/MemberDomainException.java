package revi1337.onsquad.member.domain.error;

import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
public abstract class MemberDomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;

    public MemberDomainException(ErrorCode errorCode, String finalErrorMessage) {
        super(finalErrorMessage);
        this.errorCode = errorCode;
        this.errorMessage = finalErrorMessage;
    }

    public static class InvalidEmailFormat extends MemberDomainException {

        public InvalidEmailFormat(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class InvalidPasswordFormat extends MemberDomainException {

        public InvalidPasswordFormat(ErrorCode errorCode) {
            super(errorCode, String.format(errorCode.getDescription()));
        }
    }

    public static class InvalidNicknameLength extends MemberDomainException {

        public InvalidNicknameLength(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class InvalidIntroduceLength extends MemberDomainException {

        public InvalidIntroduceLength(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }

    public static class InvalidMbti extends MemberDomainException {

        public InvalidMbti(ErrorCode errorCode) {
            super(errorCode, errorCode.getDescription());
        }
    }
}
