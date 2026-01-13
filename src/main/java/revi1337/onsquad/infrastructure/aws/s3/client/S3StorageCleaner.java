package revi1337.onsquad.infrastructure.aws.s3.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Error;

@RequiredArgsConstructor
public class S3StorageCleaner {

    public static final int BATCH_SIZE = 1000;

    private final S3Client s3Client;
    private final String bucketName;

    @Async("s3DeletionExecutor")
    public CompletableFuture<DeletedResult> deleteInBatch(List<String> paths) {
        List<ObjectIdentifier> objectIdentifiers = createObjectIdentifiers(paths);
        DeleteObjectsResponse deleteResponse = deleteBatch(objectIdentifiers, false);

        List<String> deletedPaths = getDeletedPaths(deleteResponse);
        List<String> failedPaths = getFailedPaths(deleteResponse);

        return CompletableFuture.completedFuture(new DeletedResult(deletedPaths, failedPaths));
    }

    @Async("s3DeletionExecutor")
    public CompletableFuture<List<String>> deleteInBatch(List<String> paths, boolean quiet) {
        List<ObjectIdentifier> objectIdentifiers = createObjectIdentifiers(paths);
        DeleteObjectsResponse deleteResponse = deleteBatch(objectIdentifiers, quiet);

        List<String> failedPaths = getFailedPaths(deleteResponse);

        return CompletableFuture.completedFuture(failedPaths);
    }

    private List<ObjectIdentifier> createObjectIdentifiers(List<String> paths) {
        return paths.stream()
                .map(this::createObjectIdentifier)
                .toList();
    }

    private ObjectIdentifier createObjectIdentifier(String path) {
        return ObjectIdentifier.builder()
                .key(path)
                .build();
    }

    private DeleteObjectsResponse deleteBatch(List<ObjectIdentifier> batch, boolean quiet) {
        Delete delete = Delete.builder()
                .objects(batch)
                .quiet(quiet)
                .build();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete)
                .build();

        return s3Client.deleteObjects(request);
    }

    private List<String> getDeletedPaths(DeleteObjectsResponse deleteResponse) {
        return deleteResponse.deleted().stream()
                .map(DeletedObject::key)
                .toList();
    }

    private List<String> getFailedPaths(DeleteObjectsResponse deleteResponse) {
        return deleteResponse.errors().stream()
                .map(S3Error::key)
                .toList();
    }

    public record DeletedResult(List<String> deletedPaths, List<String> failedPaths) {

    }
}
