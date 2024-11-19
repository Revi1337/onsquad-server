package revi1337.onsquad.support;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import revi1337.onsquad.config.SpringActiveProfilesResolver;
import revi1337.onsquad.config.TestSecurityConfig;

@WebMvcTest
@Import(TestSecurityConfig.class)
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
public abstract class ValidationTestSupport {

    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(final WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .alwaysDo(print())
                .build();
    }
}
