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
import revi1337.onsquad.squad_member.application.response.EnrolledSquadResponse;
import revi1337.onsquad.squad_member.application.response.SquadMemberResponse;

// TODO /crews/{crewId} 를 prefix 로 넣을까 고민 필요. 도메인 일관성을 위해 넣고싶은데 쿼리 한방더 필요할지도? crewId 없이도 리소스 식별이 가능한데.. 그럼 Squad 가 Crew 에 종속된 개념이란걸 URL 에 나타낼 수가 없고.. 하 짜증난다.
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
        List<EnrolledSquadResponse> response = squadMemberQueryService.fetchAllJoinedSquads(currentMember.id());

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @GetMapping("/squads/{squadId}/members")
    public ResponseEntity<RestResponse<List<SquadMemberResponse>>> fetchMembers(
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        List<SquadMemberResponse> response = squadMemberQueryService.fetchAllBySquadId(currentMember.id(), squadId);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    @DeleteMapping("/squads/{squadId}/me")
    public ResponseEntity<RestResponse<Void>> leaveSquad(
            @PathVariable Long squadId,
            @Authenticate CurrentMember currentMember
    ) {
        squadMemberCommandService.leaveSquad(currentMember.id(), squadId);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }
}
