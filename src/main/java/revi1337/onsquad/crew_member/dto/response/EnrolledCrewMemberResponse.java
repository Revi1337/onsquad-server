package revi1337.onsquad.crew_member.dto.response;

import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;

import java.time.LocalDateTime;

public record EnrolledCrewMemberResponse(
        String crewName,
        String nickname,
        String email,
        JoinStatus status,
        LocalDateTime requestAt
) {
    public static EnrolledCrewMemberResponse from(EnrolledCrewMemberDto enrolledCrewMemberDto) {
        return new EnrolledCrewMemberResponse(
                enrolledCrewMemberDto.crewName().getValue(),
                enrolledCrewMemberDto.nickname().getValue(),
                enrolledCrewMemberDto.email().getValue(),
                enrolledCrewMemberDto.status(),
                enrolledCrewMemberDto.createdAt()
        );
    }
}
