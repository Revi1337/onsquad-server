package revi1337.onsquad.squad.domain.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadErrorCode implements ErrorCode {

    INVALID_TITLE_LENGTH(400, "SQ001", "스쿼드 제목의 길이는 %d 자 이상 %d 자 입니다."),
    INVALID_CONTENT_LENGTH(400, "SQ002", "스쿼드 본문의 길이는 %d 자 이상 %d 자 입니다."),
    INVALID_CAPACITY_SIZE(400, "SQ003", "모집인원은 최소 %d 명 이상 %d 명 이하여야 합니다."),

    NOT_FOUND(404, "SQ004", "스쿼드를 찾을 수 없습니다."),
    NOT_ENOUGH_LEFT(400, "SQ005", "남은 정원이 없습니다."),
    SQUAD_MEMBER_UNDERFLOW(400, "SQD006", "삭제할 인원이 없습니다."),
    MISMATCH_CREW_REFERENCE(400, "SQ007", "스쿼드가 속한 크루가 일치하지 않습니다."),
    MISMATCH_SQUAD_REFERENCE(400, "SQ008", "참가자가 속한 스쿼드가 일치하지 않습니다."),
    MISMATCH_MEMBER_REFERENCE(403, "SQ009", "스쿼드 작성자 정보가 일치하지 않습니다."),
    INSUFFICIENT_DELETE_AUTHORITY(403, "SQ010", "스쿼드 삭제는 크루장 또는 스쿼드 리더만 가능합니다."),
    INSUFFICIENT_MANAGE_SQUAD_AUTHORITY(403, "SQ011", "스쿼드 관리는 스쿼드 리더만 가능합니다."),
    INSUFFICIENT_LEAVE_SQUAD_AUTHORITY(403, "SQ012", "스쿼드 리더는 권한 위임 후 탈퇴 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
