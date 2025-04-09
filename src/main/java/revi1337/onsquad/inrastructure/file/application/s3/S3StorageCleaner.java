package revi1337.onsquad.inrastructure.file.application.s3;

import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.constant.Sign;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@RequiredArgsConstructor
public class S3StorageCleaner {

    private static final int BATCH_SIZE = 1000;

    private final S3Client s3Client;
    private final String bucketName;
    private final String baseDomain;

    public void deleteInBatch(List<String> removed) {
        List<ObjectIdentifier> identifiers = removed.stream()
                .map(this::convertToImagePath)
                .map(this::createObjectIdentifier)
                .toList();

        IntStream.range(0, (identifiers.size() + BATCH_SIZE - 1) / BATCH_SIZE)
                .mapToObj(sequence -> partitionByBatchSize(sequence, identifiers))
                .forEach(this::deleteBatch);
    }

    private String convertToImagePath(String removeUrl) {
        return removeUrl.replaceFirst(baseDomain, Sign.EMPTY).substring(1);
    }

    private ObjectIdentifier createObjectIdentifier(String imagePath) {
        return ObjectIdentifier.builder()
                .key(imagePath)
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
