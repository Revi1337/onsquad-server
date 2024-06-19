package revi1337.onsquad.crew.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewConfigService;
import revi1337.onsquad.crew.dto.request.CrewAcceptRequest;
import revi1337.onsquad.crew.dto.request.CrewUpdateRequest;
import revi1337.onsquad.crew.dto.response.OwnedCrewsResponse;
import revi1337.onsquad.crew_member.dto.response.EnrolledCrewMemberResponse;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/config")
@RestController
public class CrewConfigController {

    private final CrewConfigService crewConfigService;

    /**
     * 사용자가 개설한 Crew 들
     */
    @GetMapping("/crews")
    public ResponseEntity<RestResponse<List<OwnedCrewsResponse>>> findOwnedCrews(
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<OwnedCrewsResponse> ownedCrewsResponses = crewConfigService.findOwnedCrews(authenticatedMember.toDto().getId())
                .stream()
                .map(OwnedCrewsResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(ownedCrewsResponses));
    }

    /**
     * 사용자가 생성한 Crew 에 속한 CrewMember 들 조회
     */
    @GetMapping("/crew/members")
    public ResponseEntity<RestResponse<List<EnrolledCrewMemberResponse>>> findMembersForSpecifiedCrew(
            @RequestParam String crewName,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        List<EnrolledCrewMemberResponse> enrolledCrewMemberResponses = crewConfigService.findMembersForSpecifiedCrew(crewName, authenticatedMember.toDto().getId())
                .stream()
                .map(EnrolledCrewMemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(RestResponse.success(enrolledCrewMemberResponses));
    }

    @PutMapping("/crew")
    public void updateCrew(
            @Valid @RequestPart CrewUpdateRequest crewUpdateRequest,
            @RequestPart MultipartFile file,
            @Authenticate AuthenticatedMember authenticatedMember
    ) throws IOException {
        crewConfigService.updateCrew(crewUpdateRequest.toDto(), authenticatedMember.toDto().getId(), file.getBytes());

    }

    /**
     * 미완성
     */
    @PatchMapping("/crew/accept")
    public void acceptCrewMember(
            @Valid @RequestBody CrewAcceptRequest crewAcceptRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        crewConfigService.acceptCrewMember(crewAcceptRequest.toDto(), authenticatedMember.toDto().getId());
    }
}
