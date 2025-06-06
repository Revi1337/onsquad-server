package revi1337.onsquad.inrastructure.file.application.s3.scheduler;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.inrastructure.file.application.s3.S3StorageCleaner;
import revi1337.onsquad.inrastructure.file.config.s3.properties.CloudFrontProperties;
import revi1337.onsquad.inrastructure.file.support.RecycleBin;

@ExtendWith(MockitoExtension.class)
class S3RecycleBinCleaningSchedulerTest {

    private static MockedStatic<RecycleBin> mockedStatic;

    @BeforeAll
    public static void beforeALl() {
        mockedStatic = mockStatic(RecycleBin.class);
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @Mock
    private CloudFrontProperties cloudFrontProperties;

    @Mock
    private S3StorageCleaner s3StorageCleaner;

    @InjectMocks
    private S3RecycleBinCleaningScheduler scheduler;

    @Test
    @DisplayName("S3RecycleBinCleaningScheduler 의 RecycleBin 호출을 테스트한다.")
    void success() {
        String baseDomain = "https://cloudfront.net";
        String url1 = "https://cloudfront.net/file-name-1.png";
        String url2 = "https://cloudfront.net/file-name-2.png";
        mockedStatic.when(RecycleBin::flush).thenReturn(List.of(url1, url2));
        when(cloudFrontProperties.baseDomain()).thenReturn(baseDomain);
        doNothing().when(s3StorageCleaner).deleteInBatch(anyList());

        scheduler.cleanUpRecycleBin();

        mockedStatic.verify(RecycleBin::flush);
        verify(s3StorageCleaner).deleteInBatch(anyList());
    }
}
