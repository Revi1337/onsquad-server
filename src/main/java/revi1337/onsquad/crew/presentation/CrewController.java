package revi1337.onsquad.crew.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.config.Authenticate;
import revi1337.onsquad.auth.dto.AuthenticatedMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew.application.CrewService;
import revi1337.onsquad.crew.dto.request.CrewCreateRequest;
import revi1337.onsquad.crew.dto.request.CrewJoinRequest;
import revi1337.onsquad.crew.dto.response.CrewWithMemberAndImageResponse;
import revi1337.onsquad.crew.dto.response.DuplicateCrewNameResponse;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/crew")
@RestController
public class CrewController {

    private final CrewService crewService;

    /**
     * 크루명 중복확인
     */
    @GetMapping(value = "/check")
    public ResponseEntity<RestResponse<DuplicateCrewNameResponse>> checkCrewNameDuplicate(
            @RequestParam String crewName
    ) {
        if (crewService.checkDuplicateNickname(crewName)) {
            return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(true)));
        }

        return ResponseEntity.ok().body(RestResponse.success(DuplicateCrewNameResponse.of(false)));
    }

    /**
     * 새로운 Crew 생성
     */
    @PostMapping(value = "/new")
    public ResponseEntity<Void> createNewCrew(
            @Valid @RequestPart CrewCreateRequest crewCreateRequest,
            @RequestPart MultipartFile file,
            @Authenticate AuthenticatedMember authenticatedMember
    ) throws IOException {
        crewService.createNewCrew(crewCreateRequest.toDto(authenticatedMember.toDto(), file.getBytes()));

        return ResponseEntity.status(CREATED).build();
    }

    /**
     * Crew 게시글 단일 조회
     */
    @GetMapping("")
    public ResponseEntity<RestResponse<CrewWithMemberAndImageResponse>> findCrew(
            @RequestParam String crewName
    ) {
        CrewWithMemberAndImageResponse crewResponse =
                CrewWithMemberAndImageResponse.from(crewService.findCrewByName(crewName));

        return ResponseEntity.ok().body(RestResponse.success(crewResponse));
    }

    /**
     * Crew 참가 신청
     */
    @PostMapping("/join")
    public ResponseEntity<Void> joinCrew(
            @Valid @RequestBody CrewJoinRequest crewJoinRequest,
            @Authenticate AuthenticatedMember authenticatedMember
    ) {
        crewService.joinCrew(crewJoinRequest.toDto(authenticatedMember.toDto()));

        return ResponseEntity.ok().build();
    }
}
