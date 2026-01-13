package revi1337.onsquad.infrastructure.aws.s3.model;

import lombok.Getter;

@Getter
public class FilePath {

    private Long id;
    private String path;
    private int retryCount;

    public FilePath(Long id, String path, int retryCount) {
        this.id = id;
        this.path = path;
        this.retryCount = retryCount;
    }
}
