package revi1337.onsquad.infrastructure.filesystem.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Deprecated
@Getter
@AllArgsConstructor
public enum MagicByteErrorCode implements ErrorCode {

    UNSUPPORTED_MAGIC_BYTE(400, "I001", "이미지 업로드는 %s 만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
