package revi1337.onsquad.crew.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public enum CrewErrorCode implements ErrorCode {

    INVALID_DETAIL_LENGTH(400, "CR001", "크루 상세정보의 길이는 %d 자 이상 %d 자 입니다."),
    INVALID_HASHTAGS_SIZE(400, "CR002", "해시태그의 최대 개수는 %d 개 입니다."),
    INVALID_INTRODUCE_LENGTH(400, "CR003", "크루 소개는 %d 자 이상 %d 자 입니다."),
    INVALID_NAME_LENGTH(400, "CR004", "크루명의 길이는 %d 자 이상 %d 자 입니다."),
    INVALID_HASHTAG(400, "CR005", "유효하지 않은 해시태그가 존재합니다."),

    ALREADY_EXISTS(400, "CR006", "크루가 이미 존재하여 크루를 개설할 수 없습니다."),
    NOT_FOUND(404, "CR007", "크루를 찾을 수 없습니다."),
    MISMATCH_CREW_REFERENCE(400, "CR008", "참가자가 속한 크루가 일치하지 않습니다."),
    INSUFFICIENT_UPDATE_AUTHORITY(403, "CR009", "크루 수정은 작성자만 가능합니다."),
    INSUFFICIENT_DELETE_AUTHORITY(403, "CR010", "크루 삭제는 작성자만 가능합니다."),
    INSUFFICIENT_IMAGE_UPDATE_AUTHORITY(403, "CR011", "크루 이미지 변경은 작성자만 가능합니다."),
    INSUFFICIENT_IMAGE_DELETE_AUTHORITY(403, "CR012", "크루 이미지 삭제는 작성자만 가능합니다."),
    INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY(403, "CR013", "크루 참가자 조회는 크루장만 가능합니다."),
    INSUFFICIENT_MANAGE_CREW_AUTHORITY(403, "CR014", "크루 관리는 크루 매니저 이상만 가능합니다."),
    INSUFFICIENT_LEAVE_CREW_AUTHORITY(403, "CR015", "크루장은 권한 위임 후 탈퇴 가능합니다.");

    private final int status;
    private final String code;
    private final String description;

}
