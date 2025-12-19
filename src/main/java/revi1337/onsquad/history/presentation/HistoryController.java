package revi1337.onsquad.history.presentation;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.history.application.HistoryQueryService;
import revi1337.onsquad.history.application.response.HistoryResponse;
import revi1337.onsquad.history.domain.HistoryType;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class HistoryController {

    private final HistoryQueryService historyQueryService;

    @GetMapping("/histories/me")
    public ResponseEntity<RestResponse<List<HistoryResponse>>> fetchHistories(
            @RequestParam LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) HistoryType type,
            @Authenticate CurrentMember currentMember
    ) {
        List<HistoryResponse> response = historyQueryService.fetchHistories(currentMember.id(), from, to, type);

        return ResponseEntity.ok(RestResponse.success(response));
    }
}
