package revi1337.onsquad.crew_member.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JoinStatus {

    PENDING("보류"),
    ACCEPT("수락"),
    REJECT("거절");

    private final String text;

}
