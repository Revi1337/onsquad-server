package revi1337.onsquad.inrastructure.s3.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements ErrorCode {

    FAIL_UPLOAD(500, "S3001", "S3 파일 업로드에 실패하였습니다.");

    private final int status;
    private final String code;
    private final String description;

}
