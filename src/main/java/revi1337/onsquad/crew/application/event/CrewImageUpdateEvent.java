package revi1337.onsquad.crew.application.event;

import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.domain.Crew;

public record CrewImageUpdateEvent(
        Crew crew,
        MultipartFile multipartFile
) {
}
