package revi1337.onsquad.notification.presentation;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import revi1337.onsquad.notification.application.NotificationService;

@RequiredArgsConstructor
@RequestMapping("/api/sse")
@RestController
public class NotificationController {

    private static final String LAST_EVENT_ID = "Last-Event-ID";
    private final NotificationService notificationService;

    @GetMapping(value = "/{userId}/notification", produces = TEXT_EVENT_STREAM_VALUE) // TODO EventSource는 Authorization 헤더 못보내서 프론트랑 상의 필요. + CORS 등
    public SseEmitter connect(
            @PathVariable Long userId,
            @RequestHeader(value = LAST_EVENT_ID, required = false) Long lastEventId
    ) {
        return notificationService.connect(userId, lastEventId);
    }
}
