package revi1337.onsquad.inrastructure.file.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum AttachmentErrorCode implements ErrorCode {

    UNSUPPORTED_MAGIC_BYTE(400, "I001", "이미지 업로드는 %s 만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
