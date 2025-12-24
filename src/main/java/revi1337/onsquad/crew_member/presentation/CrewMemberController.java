package revi1337.onsquad.crew_member.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_member.application.CrewMemberService;
import revi1337.onsquad.crew_member.application.response.CrewMembersWithCountResponse;
import revi1337.onsquad.crew_member.application.response.MyParticipantResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CrewMemberController {

    private final CrewMemberService crewMemberService;

    @GetMapping("/crews/{crewId}/members")
    public ResponseEntity<RestResponse<CrewMembersWithCountResponse>> fetchParticipants(
            @PathVariable Long crewId,
            @PageableDefault Pageable pageable,
            @Authenticate CurrentMember currentMember
    ) {
        CrewMembersWithCountResponse response = crewMemberService.fetchParticipants(currentMember.id(), crewId, pageable);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @GetMapping("/members/me/crew-participants")
    public ResponseEntity<RestResponse<List<MyParticipantResponse>>> fetchMyParticipatingCrews(
            @Authenticate CurrentMember currentMember
    ) {
        List<MyParticipantResponse> response = crewMemberService.fetchMyParticipatingCrews(currentMember.id());

        return ResponseEntity.ok().body(RestResponse.success(response));
    }
}
