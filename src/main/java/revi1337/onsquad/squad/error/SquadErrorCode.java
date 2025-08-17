package revi1337.onsquad.squad.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum SquadErrorCode implements ErrorCode {

    NOTFOUND(404, "SQ001", "스쿼드를 찾을 수 없습니다."),
    ALREADY_REQUEST(400, "SQ002", "%s 스쿼드에 참여요청을 한 이력이 있습니다."),
    ALREADY_JOIN(400, "SQ003", "이미 %s 스쿼드에 가입된 사용자입니다."),

    INVALID_CAPACITY_SIZE(400, "SQ004", "모집인원은 최소 %d 명 이상 %d 명 이하여야 합니다."),
    NOT_ENOUGH_LEFT(400, "SQ005", "정원이 다 찼습니다."),
    NOT_IN_CREW(400, "SQ006", "스쿼드는 %s 크루 안에 속해있지 않습니다."),
    OWNER_CANT_PARTICIPANT(400, "SQ007", "스쿼드를 만든 사람은 신청할 수 없습니다."),
    INVALID_CATEGORY(400, "SQ008", "유효하지 않은 카테고리가 존재합니다."),
    MISMATCH_REFERENCE(400, "SQ009", "스쿼드가 속한 크루와 일치하지 않습니다."),
    UNSUFFICIENT_AUTHORITY(403, "SQ010", "스쿼드 리더만 이용 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
