package revi1337.onsquad.notification.infrastructure.s3;

import java.util.List;

public record RetryExceedJson(
        List<String> exceedPaths
) {

}
