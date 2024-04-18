package revi1337.onsquad.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import revi1337.onsquad.config.SpringActiveProfilesResolver;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
public abstract class IntegrationTestSupport extends TestContainerSupport {

    protected static final String TEST_EMAIL = "test@email.com";
    protected static final String TEST_AUTH_CODE = "1111";
    protected static final String TEST_NICKNAME = "nickname";

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    @BeforeEach
    protected void setUp(final WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}
