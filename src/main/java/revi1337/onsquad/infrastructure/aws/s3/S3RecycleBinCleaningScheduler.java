package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.support.RecycleBin;
import revi1337.onsquad.infrastructure.aws.s3.support.UrlUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3RecycleBinCleaningScheduler {

    private final CloudFrontProperties cloudFrontProperties;
    private final S3StorageCleaner s3StorageCleaner;

    @Scheduled(cron = "${onsquad.api.clean-recycle-bin.schedule.expression}")
    public void cleanUpRecycleBin() {
        List<String> targetUrls = RecycleBin.flush();
        if (!targetUrls.isEmpty()) {
            List<String> purePaths = UrlUtils.extractPathExcludeFirstSlash(cloudFrontProperties.baseDomain(), targetUrls);
            try {
                log.info("S3 RecycleBin Cleanup - Total files : {}", purePaths.size());
                s3StorageCleaner.deleteInBatch(purePaths);
            } catch (Exception e) {
                log.error("Failed to Cleanup S3 RecycleBin", e);
            }
        }
    }
}
