package revi1337.onsquad.squad.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadErrorCode implements ErrorCode {

    NOT_FOUND(404, "SQ001", "스쿼드를 찾을 수 없습니다."),
    INVALID_CAPACITY_SIZE(400, "SQ002", "모집인원은 최소 %d 명 이상 %d 명 이하여야 합니다."),
    NOT_ENOUGH_LEFT(400, "SQ003", "남은 정원이 없습니다."),
    SQUAD_MEMBER_UNDERFLOW(400, "SQD004", "삭제할 인원이 없습니다."),
    INVALID_CATEGORY(400, "SQ005", "유효하지 않은 카테고리가 존재합니다."),
    MISMATCH_CREW_REFERENCE(400, "SQ006", "스쿼드가 속한 크루가 일치하지 않습니다."),
    MISMATCH_MEMBER_REFERENCE(403, "SQ007", "스쿼드 작성자 정보가 일치하지 않습니다."),
    INSUFFICIENT_DELETE_AUTHORITY(403, "SQ008", "스쿼드 삭제는 리더만 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
