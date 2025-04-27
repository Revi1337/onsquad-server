package revi1337.onsquad.member.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.PASSWORD_VALUE;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.inrastructure.file.support.RecycleBinLifeCycleManager;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepository;
import revi1337.onsquad.inrastructure.mail.support.VerificationCacheLifeCycleManager;
import revi1337.onsquad.member.application.dto.MemberJoinDto;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.member.domain.vo.Email;

@MockBean({VerificationCacheLifeCycleManager.class, RecycleBinLifeCycleManager.class})
@SpringBootTest
class MemberConcurrencyServiceTest {

    @Autowired
    private MemberCommandService memberCommandService;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @MockBean(name = "repositoryChain")
    private VerificationCodeRepository repositoryChain;

    @BeforeEach
    void setUp() {
        given(repositoryChain.isMarkedVerificationStatusWith(any(), any())).willReturn(true);
    }

    @Test
    @DisplayName("멤버 생성 동시성 테스트를 진행한다.")
    void testConcurrency() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        MemberJoinDto memberJoinDto = new MemberJoinDto(
                EMAIL_VALUE, PASSWORD_VALUE, PASSWORD_VALUE, NICKNAME_VALUE, ADDRESS_VALUE, ADDRESS_DETAIL_VALUE
        );

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    memberCommandService.newMember(memberJoinDto);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        List<Member> members = memberJpaRepository.findAllByEmail(new Email(EMAIL_VALUE));
        System.out.println(members.size());
    }
}