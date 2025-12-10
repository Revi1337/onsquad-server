package revi1337.onsquad.squad_member.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_member.application.SquadMemberCommandService;
import revi1337.onsquad.squad_member.application.SquadMemberQueryService;
import revi1337.onsquad.squad_member.presentation.dto.response.EnrolledSquadResponse;
import revi1337.onsquad.squad_member.presentation.dto.response.SquadMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SquadMemberController {

    private final SquadMemberCommandService squadMemberCommandService;
    private final SquadMemberQueryService squadMemberQueryService;

    @GetMapping("/squads/me") // TODO Presentation, Application, Persistence 테스트 보류. 페이지가 나뉠 가능성이 매우 큼
    public ResponseEntity<RestResponse<List<EnrolledSquadResponse>>> fetchAllJoinedSquads(
            @Authenticate CurrentMember currentMember
    ) {
        List<EnrolledSquadResponse> enrolledSquadResponses = squadMemberQueryService
                .fetchAllJoinedSquads(currentMember.id()).stream()
                .map(EnrolledSquadResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(enrolledSquadResponses));
    }

    @GetMapping("/squads/{squadId}/members")
    public ResponseEntity<RestResponse<List<SquadMemberResponse>>> fetchMembers(
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadMemberResponse> results = squadMemberQueryService
                .fetchAllBySquadId(currentMember.id(), squadId).stream()
                .map(SquadMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(results));
    }

    @DeleteMapping("/squads/{squadId}/me")
    public ResponseEntity<RestResponse<String>> leaveSquad(
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        squadMemberCommandService.leaveSquad(currentMember.id(), squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
