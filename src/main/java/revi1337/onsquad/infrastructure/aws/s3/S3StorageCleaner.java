package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Error;

@RequiredArgsConstructor
public class S3StorageCleaner {

    public static final int BATCH_SIZE = 1000;

    private final S3Client s3Client;
    private final String bucketName;

    @Async("s3DeletionExecutor")
    public CompletableFuture<FilePaths> deleteInBatch(FilePaths filePaths) {
        List<ObjectIdentifier> objectIdentifiers = createObjectIdentifiers(filePaths);
        DeleteObjectsResponse deleteResponse = deleteBatch(objectIdentifiers);
        FilePaths failedPaths = getFailPathsFromResponse(deleteResponse);

        return CompletableFuture.completedFuture(failedPaths);
    }

    private List<ObjectIdentifier> createObjectIdentifiers(FilePaths filePaths) {
        return filePaths.values().stream()
                .map(this::createObjectIdentifier)
                .toList();
    }

    private ObjectIdentifier createObjectIdentifier(String path) {
        return ObjectIdentifier.builder()
                .key(path)
                .build();
    }

    private DeleteObjectsResponse deleteBatch(List<ObjectIdentifier> batch) {
        Delete delete = Delete.builder()
                .objects(batch)
                .quiet(true)
                .build();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete)
                .build();

        return s3Client.deleteObjects(request);
    }

    private FilePaths getFailPathsFromResponse(DeleteObjectsResponse deleteResponse) {
        return deleteResponse.errors().stream()
                .map(S3Error::key)
                .collect(Collectors.collectingAndThen(Collectors.toList(), FilePaths::new));
    }
}
