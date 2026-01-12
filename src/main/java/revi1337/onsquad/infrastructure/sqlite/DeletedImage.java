package revi1337.onsquad.infrastructure.sqlite;

import lombok.Getter;

@Getter
public class DeletedImage {

    private Long id;
    private String path;
    private int retryCount;

    public DeletedImage(Long id, String path, int retryCount) {
        this.id = id;
        this.path = path;
        this.retryCount = retryCount;
    }
}
