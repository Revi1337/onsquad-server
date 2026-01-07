package revi1337.onsquad.infrastructure.aws.s3.event;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class FileDeleteEvent {

    private final List<String> fileUrls;

    public FileDeleteEvent(String fileUrl) {
        this(fileUrl == null ? Collections.emptyList() : List.of(fileUrl));
    }

    public FileDeleteEvent(List<String> fileUrls) {
        this.fileUrls = Collections.unmodifiableList(fileUrls);
    }
}
