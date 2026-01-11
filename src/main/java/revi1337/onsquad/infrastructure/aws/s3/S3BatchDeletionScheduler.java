package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.aws.s3.S3StorageCleaner.DeletedResult;
import revi1337.onsquad.infrastructure.sqlite.RecycleBinRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3BatchDeletionScheduler {

    private final RecycleBinRepository recycleBinRepository;
    private final S3StorageCleaner s3StorageCleaner;

    @Scheduled(cron = "${onsquad.aws.s3.delete-batch-cron}")
    public void deleteInBatch() {
        FilePaths toRemove = new FilePaths(recycleBinRepository.findAll());
        if (toRemove.isEmpty()) {
            return;
        }
        log.debug("Starting to Cleanup S3 Objects - Total Objects : {}", toRemove.size());
        List<DeletedResult> deletedResults = getDeletedResults(toRemove);
        FilePaths deletedPaths = getDeletedPaths(deletedResults, toRemove);
        FilePaths failedPaths = getFailedPaths(deletedResults, toRemove);
        if (deletedPaths.isNotEmpty()) {
            log.debug("Success Cleanup S3 Objects - Total Objects : {}", deletedPaths.size());
            recycleBinRepository.deleteByIdIn(deletedPaths.getFileIds());
        }
        if (failedPaths.isNotEmpty()) {
            log.debug("Fail to Cleanup S3 Objects - Total Objects : {}", failedPaths.size());
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

    private FilePaths getDeletedPaths(List<DeletedResult> deletedResults, FilePaths toRemove) {
        List<String> allDeleted = deletedResults.stream()
                .map(DeletedResult::deletedPaths)
                .flatMap(List::stream)
                .toList();

        return toRemove.filterByPaths(allDeleted);
    }

    private FilePaths getFailedPaths(List<DeletedResult> deletedResults, FilePaths toRemove) {
        List<String> allFailed = deletedResults.stream()
                .map(DeletedResult::failedPaths)
                .flatMap(List::stream)
                .toList();

        return toRemove.filterByPaths(allFailed);
    }
}
