package revi1337.onsquad.squad_member.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad_member.application.SquadMemberCommandService;
import revi1337.onsquad.squad_member.application.SquadMemberQueryService;
import revi1337.onsquad.squad_member.application.response.MyParticipantResponse;
import revi1337.onsquad.squad_member.application.response.SquadMemberResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SquadMemberController {

    private final SquadMemberCommandService squadMemberCommandService;
    private final SquadMemberQueryService squadMemberQueryService;

    @GetMapping("/squads/{squadId}/members")
    public ResponseEntity<RestResponse<List<SquadMemberResponse>>> fetchParticipants(
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadMemberResponse> response = squadMemberQueryService.fetchParticipants(currentMember.id(), squadId);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @PatchMapping("/squads/{squadId}/members/{targetMemberId}/leader")
    public ResponseEntity<RestResponse<Void>> delegateLeader(
            @PathVariable Long squadId,
            @PathVariable Long targetMemberId,
            @Authenticate CurrentMember currentMember
    ) {
        squadMemberCommandService.delegateLeader(currentMember.id(), squadId, targetMemberId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/squads/{squadId}/members/me")
    public ResponseEntity<RestResponse<Void>> leaveSquad(
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        squadMemberCommandService.leaveSquad(currentMember.id(), squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @DeleteMapping("/squads/{squadId}/members/{targetMemberId}")
    public ResponseEntity<RestResponse<Void>> kickOutMember(
            @PathVariable Long squadId,
            @PathVariable Long targetMemberId,
            @Authenticate CurrentMember currentMember
    ) {
        squadMemberCommandService.kickOutMember(currentMember.id(), squadId, targetMemberId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/members/me/squad-participants")
    public ResponseEntity<RestResponse<List<MyParticipantResponse>>> fetchMyParticipatingSquads(
            @Authenticate CurrentMember currentMember
    ) {
        List<MyParticipantResponse> response = squadMemberQueryService.fetchMyParticipatingSquads(currentMember.id());

        return ResponseEntity.ok().body(RestResponse.success(response));
    }
}
