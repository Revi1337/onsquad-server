package revi1337.onsquad.crew.domain.event;

import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.domain.entity.Crew;

public record CrewImageUpdateEvent(
        Crew crew,
        MultipartFile multipartFile
) {

}
