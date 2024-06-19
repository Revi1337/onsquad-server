package revi1337.onsquad.crew_member.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;

import java.time.LocalDateTime;

public record EnrolledCrewMemberDto(
        Name crewName,
        Nickname nickname,
        Email email,
        JoinStatus status,
        LocalDateTime createdAt
) {
    @QueryProjection
    public EnrolledCrewMemberDto(Name crewName, Nickname nickname, Email email, JoinStatus status, LocalDateTime createdAt) {
        this.crewName = crewName;
        this.nickname = nickname;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }
}
