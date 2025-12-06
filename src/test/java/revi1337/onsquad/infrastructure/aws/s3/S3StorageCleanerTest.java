package revi1337.onsquad.infrastructure.aws.s3;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;

class S3StorageCleanerTest extends ApplicationLayerWithTestContainerSupport {

    @SpyBean
    private S3Client s3Client;

    @Autowired
    private S3StorageCleaner s3StorageCleaner;

    @Test
    @DisplayName("삭제할 파일이 총 2001 개 있다면, 1000 개씩 총 3번 삭제한다.")
    void success() {
        List<String> toBeDeleted = IntStream.rangeClosed(1, 2001)
                .mapToObj(sequence -> String.format("assets/file_%d.png", sequence))
                .toList();

        s3StorageCleaner.deleteInBatch(toBeDeleted);

        verify(s3Client, times(3)).deleteObjects(any(DeleteObjectsRequest.class));
    }
}
