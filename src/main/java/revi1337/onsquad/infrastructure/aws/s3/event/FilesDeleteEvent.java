package revi1337.onsquad.infrastructure.aws.s3.event;

import java.util.List;

public record FilesDeleteEvent(
        List<String> fileUrls
) {

}
