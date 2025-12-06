package revi1337.onsquad.infrastructure.aws.s3.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.aws.s3.support.RecycleBin;

@Component
public class FileDeleteEventListener {

    @EventListener(FileDeleteEvent.class)
    public void handleFileDeleteEvent(FileDeleteEvent event) {
        RecycleBin.append(event.fileUrl());
    }
}
