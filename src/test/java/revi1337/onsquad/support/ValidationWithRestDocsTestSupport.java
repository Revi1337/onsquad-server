package revi1337.onsquad.support;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import revi1337.onsquad.config.SpringActiveProfilesResolver;
import revi1337.onsquad.config.TestSecurityConfig;

@Import({TestSecurityConfig.class})
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
public abstract class ValidationWithRestDocsTestSupport extends RestDocumentationSupport {
}
