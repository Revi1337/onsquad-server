package revi1337.onsquad.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.util.StringUtils;

public abstract class FileNamingUtils {

    public static String createUuidName(String filePathAndName) {
        if (!StringUtils.hasText(filePathAndName)) {
            return UUID.randomUUID().toString();
        }

        Path path = Paths.get(filePathAndName);
        String extension = StringUtils.getFilenameExtension(path.getFileName().toString());
        String uuidName = StringUtils.hasText(extension) ? UUID.randomUUID() + "." + extension : UUID.randomUUID().toString();

        Path resolvedPath = path.resolveSibling(uuidName);

        return StreamSupport.stream(resolvedPath.spliterator(), false)
                .map(Path::toString)
                .collect(Collectors.joining("/"));
    }
}
