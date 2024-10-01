package revi1337.onsquad.crew_member.presentation.dto.response;

import revi1337.onsquad.crew_member.application.dto.Top5CrewMemberDto;

import java.time.LocalDateTime;

public record Top5CrewMemberResponse(
        Long crewId,
        int rank,
        int counter,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime participateAt
) {
    public static Top5CrewMemberResponse from(Top5CrewMemberDto top5CrewMemberDto) {
        return new Top5CrewMemberResponse(
                top5CrewMemberDto.crewId(),
                top5CrewMemberDto.rank(),
                top5CrewMemberDto.counter(),
                top5CrewMemberDto.memberId(),
                top5CrewMemberDto.nickname(),
                top5CrewMemberDto.mbti(),
                top5CrewMemberDto.participateAt()
        );
    }
}
