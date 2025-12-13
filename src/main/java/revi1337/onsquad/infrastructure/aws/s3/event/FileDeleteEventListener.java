package revi1337.onsquad.infrastructure.aws.s3.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.util.UrlUtils;
import revi1337.onsquad.infrastructure.sqlite.RecycleBinRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileDeleteEventListener {

    private final RecycleBinRepository recycleBinRepository;
    private final CloudFrontProperties cloudFrontProperties;

    @Async("fileDeleteEventExecutor")
    @TransactionalEventListener(value = FileDeleteEvent.class, fallbackExecution = true)
    public void onDeleteFile(FileDeleteEvent event) {
        String filePath = UrlUtils.stripPrefixAndLeadingSlash(cloudFrontProperties.baseDomain(), event.fileUrl());
        recycleBinRepository.save(filePath);
        log.debug("file path({}) has been stored in SQLite for batch deletion", filePath);
    }
}
