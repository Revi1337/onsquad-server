package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.aws.s3.S3StorageCleaner.DeletedResult;
import revi1337.onsquad.infrastructure.sqlite.ImageRecycleBinRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3BatchDeletionScheduler {

    public static final int MAX_RETRY_COUNT = 5;

    private final ImageRecycleBinRepository imageRecyclebinRepository;
    private final S3StorageCleaner s3StorageCleaner;
    private final S3FailNotificationProvider notificationProvider;

    @Scheduled(cron = "${onsquad.aws.s3.delete-batch-cron}")
    public void deleteInBatch() {
        FilePaths toBeRemove = new FilePaths(imageRecyclebinRepository.findAll());
        if (toBeRemove.isEmpty()) {
            return;
        }
        log.debug("Starting to Cleanup S3 Objects - Total Objects : {}", toBeRemove.size());
        List<DeletedResult> deletedResults = getDeletedResults(toBeRemove);
        FilePaths deletedPaths = getDeletedPaths(toBeRemove, deletedResults);
        FilePaths failedPaths = getFailedPaths(toBeRemove, deletedResults);
        if (deletedPaths.isNotEmpty()) {
            log.debug("Success Cleanup S3 Objects - Total Objects : {}", deletedPaths.size());
            imageRecyclebinRepository.deleteByIdIn(deletedPaths.getFileIds());
        }
        if (failedPaths.isNotEmpty()) {
            log.debug("Fail to Cleanup S3 Objects - Total Objects : {}", failedPaths.size());
            imageRecyclebinRepository.incrementRetryCount(failedPaths.getFileIds());
            FilePaths exceedFilePaths = new FilePaths(imageRecyclebinRepository.findByRetryCountLargerThan(MAX_RETRY_COUNT));
            if (exceedFilePaths.isEmpty()) {
                return;
            }
            notificationProvider.sendExceedRetryAlert(exceedFilePaths.pathValues());
            imageRecyclebinRepository.deleteByIdIn(exceedFilePaths.getFileIds());
        }
    }

    private List<DeletedResult> getDeletedResults(FilePaths toRemove) {
        List<CompletableFuture<DeletedResult>> futures = toRemove.partition(S3StorageCleaner.BATCH_SIZE).stream()
                .map(FilePaths::pathValues)
                .map(s3StorageCleaner::deleteInBatch)
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private FilePaths getDeletedPaths(FilePaths toBeRemove, List<DeletedResult> deletedResults) {
        List<String> allDeletedPaths = deletedResults.stream()
                .map(DeletedResult::deletedPaths)
                .flatMap(List::stream)
                .toList();

        return toBeRemove.filterByPaths(allDeletedPaths);
    }

    private FilePaths getFailedPaths(FilePaths toBeRemove, List<DeletedResult> deletedResults) {
        List<String> allFailedPaths = deletedResults.stream()
                .map(DeletedResult::failedPaths)
                .flatMap(List::stream)
                .toList();

        return toBeRemove.filterByPaths(allFailedPaths);
    }
}
