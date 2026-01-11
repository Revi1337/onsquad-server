package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
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
        FilePaths totalFilePaths = new FilePaths(recycleBinRepository.findAll());
        if (totalFilePaths.isEmpty()) {
            return;
        }
        recycleBinRepository.deleteAllInBatch();
        log.debug("Starting to Cleanup S3 Objects - Total Objects : {}", totalFilePaths.size());
        List<FilePaths> partitionedFilePaths = totalFilePaths.partition(S3StorageCleaner.BATCH_SIZE);
        List<CompletableFuture<FilePaths>> futures = partitionedFilePaths.stream()
                .map(s3StorageCleaner::deleteInBatch)
                .toList();

        FilePaths failedPaths = futures.stream()
                .map(CompletableFuture::join)
                .map(FilePaths::values)
                .flatMap(List::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), FilePaths::new));

        log.debug("Success Cleanup S3 Objects - Total Objects : {}", totalFilePaths.size() - failedPaths.size());
        if (failedPaths.isNotEmpty()) {
            log.debug("Fail to Cleanup S3 Objects - Total Objects : {}", failedPaths.size());
            recycleBinRepository.insertBatch(failedPaths.values());
        }
    }
}
