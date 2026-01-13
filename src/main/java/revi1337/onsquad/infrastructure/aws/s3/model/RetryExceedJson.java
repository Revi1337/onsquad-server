package revi1337.onsquad.infrastructure.aws.s3.model;

import java.util.List;

public record RetryExceedJson(
        List<String> exceedPaths
) {

}
