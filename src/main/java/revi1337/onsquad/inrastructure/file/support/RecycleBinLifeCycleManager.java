package revi1337.onsquad.inrastructure.file.support;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecycleBinLifeCycleManager {

    private static final String RECYCLE_BIN_BACKUP_PATH = "backup/recycle_bin_backup.txt";
    private static final String RESTORE_ERROR_LOG = "Error Occur While Restoring Recycle Bin";
    private static final String RESTORE_LOG_FORMAT = "Restored Recycle Bin - path : {}";
    private static final String WRITING_ERROR_LOG = "Error Occur While Writing Recycle Bin Content";
    private static final String WRITING_LOG = "Writing Recycle Bin Content";

    @EventListener(ApplicationReadyEvent.class)
    public void restoreRecycleBin() {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(RECYCLE_BIN_BACKUP_PATH), UTF_8)) {
            bufferedReader.lines().forEach(RecycleBin::append);
            log.info(RESTORE_LOG_FORMAT, RECYCLE_BIN_BACKUP_PATH);
        } catch (IOException e) {
            log.error(RESTORE_ERROR_LOG, e);
        }
    }

    @EventListener(ContextClosedEvent.class)
    public void storeRecycleBin() {
        try {
            log.info(WRITING_LOG);
            Files.write(Paths.get(RECYCLE_BIN_BACKUP_PATH), RecycleBin.flush(), UTF_8);
        } catch (IOException e) {
            log.error(WRITING_ERROR_LOG, e);
        }
    }
}
