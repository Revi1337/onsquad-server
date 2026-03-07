package revi1337.onsquad.infrastructure.aws.s3.client;

import java.util.List;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Error;

@RequiredArgsConstructor
public class S3StorageCleaner {

    private final S3Client s3Client;
    private final String bucketName;

    public DeletedResult deleteInBatch(List<String> paths) {
        List<ObjectIdentifier> objectIdentifiers = createObjectIdentifiers(paths);
        DeleteObjectsResponse deleteResponse = deleteBatch(objectIdentifiers, false);

        List<String> deletedPaths = getDeletedPaths(deleteResponse);
        List<String> failedPaths = getFailedPaths(deleteResponse);

        return new DeletedResult(deletedPaths, failedPaths);
    }

    public DeletedResult deleteInBatchQuietly(List<String> paths) {
        List<ObjectIdentifier> objectIdentifiers = createObjectIdentifiers(paths);
        DeleteObjectsResponse deleteResponse = deleteBatch(objectIdentifiers, true);

        List<String> deletedPaths = getDeletedPaths(deleteResponse);
        List<String> failedPaths = getFailedPaths(deleteResponse);

        return new DeletedResult(deletedPaths, failedPaths);
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
