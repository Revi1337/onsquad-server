package revi1337.onsquad.infrastructure.aws.s3;

import lombok.Getter;

@Getter
public class FilePath {

    private Long id;
    private String path;

    public FilePath(Long id, String path) {
        this.id = id;
        this.path = path;
    }
}
