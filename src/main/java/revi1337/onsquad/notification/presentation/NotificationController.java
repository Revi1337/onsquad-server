package revi1337.onsquad.notification.presentation;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import revi1337.onsquad.auth.support.Authenticate;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.notification.application.NotificationQueryService;
import revi1337.onsquad.notification.application.NotificationService;
import revi1337.onsquad.notification.application.response.NotificationResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class NotificationController {

    private static final String LAST_EVENT_ID = "Last-Event-ID";

    private final NotificationService notificationService;
    private final NotificationQueryService notificationQueryService;

    @GetMapping(value = "/sse/{userId}/notification", produces = TEXT_EVENT_STREAM_VALUE) // TODO EventSource는 Authorization 헤더 못보내서 프론트랑 상의 필요. + CORS 등
    public SseEmitter connect(
            @PathVariable Long userId,
            @RequestHeader(value = LAST_EVENT_ID, required = false) Long lastEventId
    ) {
        return notificationService.connect(userId, lastEventId);
    }

    @GetMapping("/notifications")
    public ResponseEntity<RestResponse<List<NotificationResponse>>> fetchNotifications(
            @Authenticate CurrentMember currentMember,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<NotificationResponse> response = notificationQueryService
                .fetchNotifications(currentMember.id(), PageRequest.of(page, size, Sort.by("id").descending()));

        return ResponseEntity.ok(RestResponse.success(response));
    }
}
