package revi1337.onsquad.infrastructure.aws.s3;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@RequiredArgsConstructor
public class S3StorageCleaner {

    private static final int BATCH_SIZE = 1000;

    private final S3Client s3Client;
    private final String bucketName;

    @Async("s3DeletionExecutor")
    public void deleteInBatch(List<String> targetPaths) {
        List<List<ObjectIdentifier>> batches = partition(targetPaths);
        batches.forEach(this::deleteBatch);
    }

    private List<List<ObjectIdentifier>> partition(List<String> targetPaths) {
        List<List<ObjectIdentifier>> batches = new ArrayList<>();
        for (int i = 0; i < targetPaths.size(); i += BATCH_SIZE) {
            List<String> subLists = targetPaths.subList(i, Math.min(i + BATCH_SIZE, targetPaths.size()));
            List<ObjectIdentifier> batch = createObjectIdentifiers(subLists);
            batches.add(batch);
        }
        return batches;
    }

    private List<ObjectIdentifier> createObjectIdentifiers(List<String> path) {
        return path.stream()
                .map(this::createObjectIdentifier)
                .toList();
    }

    private ObjectIdentifier createObjectIdentifier(String path) {
        return ObjectIdentifier.builder()
                .key(path)
                .build();
    }

    private void deleteBatch(List<ObjectIdentifier> batch) {
        Delete delete = Delete.builder()
                .objects(batch)
                .build();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete)
                .build();

        s3Client.deleteObjects(request);
    }
}
