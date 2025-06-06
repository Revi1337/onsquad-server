package revi1337.onsquad.inrastructure.file.support;

import java.util.List;
import revi1337.onsquad.common.constant.Sign;

public abstract class UrlUtils {

    public static List<String> extractPathExcludeFirstSlash(String delimiter, List<String> urls) {
        return urls.stream()
                .map(removeUrl -> removeUrl.replaceFirst(delimiter, Sign.EMPTY).substring(1))
                .toList();
    }
}
