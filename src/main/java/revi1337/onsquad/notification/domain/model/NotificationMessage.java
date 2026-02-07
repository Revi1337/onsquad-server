package revi1337.onsquad.notification.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationMessage(
        Long id,
        NotificationTopic topic,
        NotificationDetail detail,
        Long publisherId,
        Long receiverId,
        Boolean read,
        JsonNode payload
) {

}
