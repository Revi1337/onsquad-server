package revi1337.onsquad.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import revi1337.onsquad.auth.application.JsonWebTokenEvaluator;
import revi1337.onsquad.common.config.WebMvcConfig;

@Import(WebMvcConfig.class)
@WebMvcTest
@ExtendWith({RestDocumentationExtension.class})
public abstract class RestDocumentationSupport {

    protected static final String AUTHORIZATION_HEADER_KEY = HttpHeaders.AUTHORIZATION;
    protected static final String AUTHORIZATION_HEADER_VALUE = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpZGVudGlmaWVyIjoxLCJzdWIiOiJhY2Nlc3NfdG9rZW4iLCJpc3MiOiJPTlNRVUFELVNFUlZFUiIsImlhdCI6MTcxODE5MTI4NSwiZXhwIjoxNzE4MTkxMjkwfQ.HHA_2BPtDb1BPD-mOKUj9eHED3BqyHryGulwa1di2PK-Kt-fiFpaAUJjrTCLu9cZHLzmGgn47gXUBw6H8arXPg";

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @MockBean private JsonWebTokenEvaluator jsonWebTokenEvaluator;

    @BeforeEach
    protected void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider provider
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}
