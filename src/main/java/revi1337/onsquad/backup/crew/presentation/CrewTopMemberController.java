package revi1337.onsquad.backup.crew.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.backup.crew.application.CrewTopMemberService;
import revi1337.onsquad.backup.crew.presentation.dto.Top5CrewMemberResponse;
import revi1337.onsquad.common.dto.RestResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CrewTopMemberController {

    private final CrewTopMemberService crewTopMemberService;

    @GetMapping("/crews/{crewId}/top")
    public ResponseEntity<RestResponse<List<Top5CrewMemberResponse>>> findTop5CrewMembers(
            @PathVariable Long crewId,
            @Authenticate AuthMemberAttribute authMemberAttribute
    ) {
        List<Top5CrewMemberResponse> top5CrewMembers = crewTopMemberService
                .findTop5CrewMembers(authMemberAttribute.id(), crewId).stream()
                .map(Top5CrewMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(top5CrewMembers));
    }
}
