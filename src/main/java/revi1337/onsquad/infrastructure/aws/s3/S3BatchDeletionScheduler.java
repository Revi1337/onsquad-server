package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.sqlite.RecycleBinRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3BatchDeletionScheduler {

    private final RecycleBinRepository recycleBinRepository;
    private final S3StorageCleaner s3StorageCleaner;

    @Scheduled(cron = "${onsquad.aws.s3.delete-batch-cron}")
    public void deleteInBatch() {
        List<String> targetPaths = recycleBinRepository.findAll();
        if (targetPaths.isEmpty()) {
            return;
        }
        log.info("Starting to Cleanup S3 Objects  - Total Objects : {}", targetPaths.size());
        s3StorageCleaner.deleteInBatch(targetPaths);
    }
}
