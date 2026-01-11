package revi1337.onsquad.infrastructure.aws.s3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilePaths {

    private final List<String> filePaths;

    public FilePaths(List<String> filePaths) {
        this.filePaths = Collections.unmodifiableList(filePaths);
    }

    public boolean isEmpty() {
        return filePaths.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public int size() {
        return filePaths.size();
    }

    public List<FilePaths> partition(int size) {
        List<FilePaths> partitions = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i += size) {
            List<String> partition = filePaths.subList(i, Math.min(i + size, filePaths.size()));
            partitions.add(new FilePaths(partition));
        }
        return partitions;
    }

    public List<String> values() {
        return filePaths;
    }
}
