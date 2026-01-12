package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.aws.s3.S3StorageCleaner.DeletedResult;
import revi1337.onsquad.infrastructure.sqlite.DeletedImage;
import revi1337.onsquad.infrastructure.sqlite.ImageRecycleBinRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageCleanupProcessor {

    public static final int MAX_RETRY_COUNT = 5;

    private final ImageRecycleBinRepository imageRecyclebinRepository;
    private final S3StorageCleaner s3StorageCleaner;

    public FilePaths fetchCleanupTargets() {
        List<FilePath> targets = imageRecyclebinRepository.findAll().stream()
                .map(this::convert)
                .toList();

        return new FilePaths(targets);
    }

    public CleanupResult processCleanup(FilePaths targets) {
        return CleanupResult.of(targets, dispatchDeleteRequests(targets));
    }

    public FilePaths handleFailedResults(FilePaths failedPaths) {
        imageRecyclebinRepository.incrementRetryCount(failedPaths.getFileIds());

        List<FilePath> exceedPaths = imageRecyclebinRepository.findByRetryCountLargerThan(MAX_RETRY_COUNT).stream()
                .map(this::convert)
                .toList();

        return new FilePaths(exceedPaths);
    }

    public void clearBin(FilePaths paths) {
        imageRecyclebinRepository.deleteByIdIn(paths.getFileIds());
    }

    private List<DeletedResult> dispatchDeleteRequests(FilePaths targets) {
        List<CompletableFuture<DeletedResult>> futures = targets.partition(S3StorageCleaner.BATCH_SIZE).stream()
                .map(FilePaths::pathValues)
                .map(s3StorageCleaner::deleteInBatch)
                .toList();

        return futures.stream().map(CompletableFuture::join).toList();
    }

    private FilePath convert(DeletedImage deletedImage) {
        return new FilePath(deletedImage.getId(), deletedImage.getPath(), deletedImage.getRetryCount());
    }

    public record CleanupResult(FilePaths success, FilePaths failure) {

        public static CleanupResult of(FilePaths targets, List<DeletedResult> results) {
            List<String> successPaths = results.stream().flatMap(r -> r.deletedPaths().stream()).toList();
            List<String> failedPaths = results.stream().flatMap(r -> r.failedPaths().stream()).toList();

            return new CleanupResult(targets.filterByPaths(successPaths), targets.filterByPaths(failedPaths));
        }
    }
}
