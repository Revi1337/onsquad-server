package revi1337.onsquad.inrastructure.file.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.inrastructure.file.support.RecycleBin;

@Component
public class FileDeleteEventListener {

    @EventListener(FileDeleteEvent.class)
    public void handleFileDeleteEvent(FileDeleteEvent event) {
        RecycleBin.append(event.fileUrl());
    }
}
