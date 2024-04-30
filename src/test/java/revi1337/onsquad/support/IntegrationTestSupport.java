package revi1337.onsquad.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import revi1337.onsquad.config.SpringActiveProfilesResolver;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
public abstract class IntegrationTestSupport extends TestContainerSupport {

    protected static final String TEST_EMAIL = "test@email.com";
    protected static final String TEST_AUTH_CODE = "1111";
    protected static final String TEST_NICKNAME = "nickname";
    protected static final String TEST_PASSWORD = "12345!@asa";
    protected static final CharSequence TEST_BCRYPT_PASSWORD = "{bcrypt}$2a$10$xCazQzTNODXcf.6Gj83v5.hcFiW0o/zO4YCyj5ZHUzUQUPZdm4H6m";

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    @BeforeEach
    protected void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider provider
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}

//    @BeforeEach
//    protected void setUp(final WebApplicationContext context,
//                         final RestDocumentationContextProvider provider) throws ServletException {
//        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
//        delegateProxyFilter.init(new MockFilterConfig(context.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
//                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
//                .alwaysDo(MockMvcResultHandlers.print())
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))
//                .addFilter(delegateProxyFilter)
//                .build();
//    }
