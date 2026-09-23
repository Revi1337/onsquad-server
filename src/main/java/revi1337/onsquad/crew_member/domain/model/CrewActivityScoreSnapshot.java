package revi1337.onsquad.crew_member.domain.model;

public record CrewActivityScoreSnapshot(
        Long crewId,
        Long memberId,
        int weight
) {
}
