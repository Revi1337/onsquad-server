package revi1337.onsquad.crew_member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.crew_member.application.CrewMemberService;
import revi1337.onsquad.crew_member.dto.CrewMemberDto;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/crew")
@RestController
public class CrewMemberController {

    private final CrewMemberService crewMemberService;

    @GetMapping("/members")
    public ResponseEntity<Void> findEnrolledCrewMembers(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<CrewMemberDto> enrolledCrewMembers = crewMemberService.findEnrolledCrewMembers(authenticatedMember.toDto().getId());
        return ResponseEntity.ok().build();
    }
}
