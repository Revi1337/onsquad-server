package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.infrastructure.aws.s3.util.UrlUtils;
import revi1337.onsquad.infrastructure.sqlite.RecycleBinRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileDeleteEventListener {

    private final RecycleBinRepository recycleBinRepository;
    private final CloudFrontProperties cloudFrontProperties;

    @Async("fileDeletionRecorder")
    @TransactionalEventListener(value = FileDeleteEvent.class, fallbackExecution = true)
    public void recordFileDeletion(FileDeleteEvent event) {
        if (CollectionUtils.isEmpty(event.getFileUrls())) {
            return;
        }
        List<String> filePaths = extractPaths(event.getFileUrls());
        recycleBinRepository.insertBatch(filePaths);
        log.debug("{} file paths have been stored in SQLite for batch deletion", filePaths.size());
    }

    private List<String> extractPaths(List<String> imageUrls) {
        return imageUrls.stream()
                .map(this::extractPath)
                .toList();
    }

    private String extractPath(String imageUrl) {
        return UrlUtils.stripPrefixAndLeadingSlash(cloudFrontProperties.baseDomain(), imageUrl);
    }
}
