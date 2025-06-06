package revi1337.onsquad.inrastructure.file.application.s3;

import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@RequiredArgsConstructor
public class S3StorageCleaner {

    private static final int BATCH_SIZE = 1000;

    private final S3Client s3Client;
    private final String bucketName;

    public void deleteInBatch(List<String> targetPaths) {
        List<ObjectIdentifier> identifiers = targetPaths.stream()
                .map(this::createObjectIdentifier)
                .toList();

        IntStream.range(0, (identifiers.size() + BATCH_SIZE - 1) / BATCH_SIZE)
                .mapToObj(sequence -> partitionByBatchSize(sequence, identifiers))
                .forEach(this::deleteBatch);
    }

    private ObjectIdentifier createObjectIdentifier(String path) {
        return ObjectIdentifier.builder()
                .key(path)
                .build();
    }

    private List<ObjectIdentifier> partitionByBatchSize(int sequence, List<ObjectIdentifier> identifiers) {
        return identifiers.subList(
                sequence * BATCH_SIZE,
                Math.min((sequence + 1) * BATCH_SIZE, identifiers.size())
        );
    }

    private void deleteBatch(List<ObjectIdentifier> batch) {
        Delete delete = Delete.builder().objects(batch).build();
        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete)
                .build();

        s3Client.deleteObjects(request);
    }
}
