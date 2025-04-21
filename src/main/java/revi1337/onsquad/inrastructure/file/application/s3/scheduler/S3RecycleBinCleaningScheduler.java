package revi1337.onsquad.inrastructure.file.application.s3.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.inrastructure.file.application.s3.S3StorageCleaner;
import revi1337.onsquad.inrastructure.file.support.RecycleBin;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3RecycleBinCleaningScheduler {

    private final S3StorageCleaner s3StorageCleaner;

    @Scheduled(cron = "${onsquad.api.clean-recycle-bin.schedule.expression}")
    public void flushRecycleBin() {
        List<String> removed = RecycleBin.flush();
        if (!removed.isEmpty()) {
            try {
                log.info("S3 Recycle Bin Cleanup - Total files : {}", removed.size());
                s3StorageCleaner.deleteInBatch(removed);
            } catch (Exception e) {
                log.error("Failed to clean S3 recycle bin", e);
            }
        }
    }
}