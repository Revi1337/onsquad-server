package revi1337.onsquad.member.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PASSWORD_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import revi1337.onsquad.inrastructure.file.support.RecycleBinLifeCycleManager;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeExpiringMapRepository;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRedisRepository;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepositoryCandidates;
import revi1337.onsquad.inrastructure.mail.support.VerificationCacheLifeCycleManager;
import revi1337.onsquad.member.presentation.dto.request.MemberJoinRequest;

@AutoConfigureMockMvc
@MockBean({VerificationCacheLifeCycleManager.class, RecycleBinLifeCycleManager.class})
@SpringBootTest
public class MemberConcurrencyControllerTest {

    @MockBean
    private VerificationCodeRedisRepository redisRepository;

    @MockBean
    private VerificationCodeExpiringMapRepository expiringMapRepository;

    @MockBean
    private VerificationCodeRepositoryCandidates repositoryChain;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(final WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("동시 사용자 생성 시, 하나만 성공하고 나머지는 실패한다.")
    @Test
    void concurrentMemberJoin() throws Exception {
        MemberJoinRequest JOIN_REQUEST = new MemberJoinRequest(
                REVI_EMAIL_VALUE, REVI_PASSWORD_VALUE,
                REVI_PASSWORD_VALUE, REVI_NICKNAME_VALUE, REVI_ADDRESS_VALUE, REVI_ADDRESS_DETAIL_VALUE
        );

        given(repositoryChain.isMarkedVerificationStatusWith(eq(REVI_EMAIL_VALUE), eq(VerificationStatus.SUCCESS)))
                .willReturn(true);
        given(redisRepository.isMarkedVerificationStatusWith(eq(REVI_EMAIL_VALUE), eq(VerificationStatus.SUCCESS)))
                .willReturn(true);
        given(expiringMapRepository.isMarkedVerificationStatusWith(eq(REVI_EMAIL_VALUE),
                eq(VerificationStatus.SUCCESS)))
                .willReturn(true);

        List<String> responses = Collections.synchronizedList(new ArrayList<>());
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {

                    String content = objectMapper.writeValueAsString(JOIN_REQUEST);
                    System.out.println(content);
                    MockHttpServletResponse response = mockMvc.perform(post("/api/members")
                                    .content(content)
                                    .contentType(APPLICATION_JSON))
                            .andReturn().getResponse();

                    responses.add(response.getContentAsString());
                } catch (Exception e) {
                    responses.add("예외 발생: " + e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        System.out.println("===== 모든 요청 결과 =====");
        responses.forEach(System.out::println);
    }
}
