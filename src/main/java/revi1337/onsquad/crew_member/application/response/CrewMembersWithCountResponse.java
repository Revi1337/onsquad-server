package revi1337.onsquad.crew_member.application.response;

import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.crew_member.domain.result.CrewMemberWithCountResult;

public record CrewMembersWithCountResponse(
        long memberCount,
        List<CrewMemberResponse> members
) {

    public static CrewMembersWithCountResponse from(List<CrewMemberWithCountResult> members) {
        if (members.isEmpty()) {
            return new CrewMembersWithCountResponse(0, new ArrayList<>());
        }
        return new CrewMembersWithCountResponse(
                members.get(0).memberCount(),
                members.stream()
                        .map(result -> CrewMemberResponse.from(result.member()))
                        .toList()
        );
    }
}
