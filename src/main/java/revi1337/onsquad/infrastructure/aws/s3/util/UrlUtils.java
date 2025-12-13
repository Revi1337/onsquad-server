package revi1337.onsquad.infrastructure.aws.s3.util;

import java.util.List;
import revi1337.onsquad.common.constant.Sign;

public abstract class UrlUtils {

    public static String stripPrefixAndLeadingSlash(String prefix, String url) {
        return url.replaceFirst(prefix, Sign.EMPTY).substring(1);
    }

    public static List<String> stripPrefixAndLeadingSlash(String prefix, List<String> urls) {
        return urls.stream()
                .map(url -> stripPrefixAndLeadingSlash(prefix, url))
                .toList();
    }
}
