package revi1337.onsquad.inrastructure.file.application.s3.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.inrastructure.file.application.s3.S3StorageCleaner;
import revi1337.onsquad.inrastructure.file.config.s3.properties.CloudFrontProperties;
import revi1337.onsquad.inrastructure.file.support.RecycleBin;
import revi1337.onsquad.inrastructure.file.support.UrlUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3RecycleBinCleaningScheduler {

    private final CloudFrontProperties cloudFrontProperties;
    private final S3StorageCleaner s3StorageCleaner;

    @Scheduled(cron = "${onsquad.api.clean-recycle-bin.schedule.expression}")
    public void cleanUpRecycleBin() {
        List<String> targetUrls = RecycleBin.flush();
        if (targetUrls.isEmpty()) {
            return;
        }
        List<String> purePaths = UrlUtils.extractPathExcludeFirstSlash(cloudFrontProperties.baseDomain(), targetUrls);
        try {
            log.info("S3 RecycleBin Cleanup - Total files : {}", purePaths.size());
            s3StorageCleaner.deleteInBatch(purePaths);
        } catch (Exception e) {
            log.error("Failed to Cleanup S3 RecycleBin", e);
        }
    }
}
