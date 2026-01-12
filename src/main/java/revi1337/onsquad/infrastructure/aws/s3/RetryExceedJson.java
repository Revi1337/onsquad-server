package revi1337.onsquad.infrastructure.aws.s3;

import java.util.List;

public record RetryExceedJson(
        List<String> exceedPaths
) {

}
