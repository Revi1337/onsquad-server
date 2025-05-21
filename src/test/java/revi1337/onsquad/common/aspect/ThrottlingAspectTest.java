package revi1337.onsquad.common.aspect;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.error.exception.CommonBusinessException;

class ThrottlingAspectTest extends ApplicationLayerTestSupport {

    @Autowired
    private ThrottlingAspect throttlingAspect;

    private TestService testService;

    @BeforeEach
    void setUp() {
        TestService testService = new TestService();
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(testService);
        aspectJProxyFactory.setProxyTargetClass(true);
        aspectJProxyFactory.addAspect(throttlingAspect);
        this.testService = aspectJProxyFactory.getProxy();
    }

    @Test
    @DisplayName("처음 시도하는 요청이면 예외가 발생하지 않는다.")
    void success() {
        Long crewId = 1L;
        Long memberId = 2L;

        testService.testMethod(crewId, memberId);
    }

    @Test
    @DisplayName("만료시간 안에 2번 이상 동일한 요청을 보내면 false 를 반환한다.")
    void fail() {
        Long crewId = 2L;
        Long memberId = 3L;
        testService.testMethod(crewId, memberId);

        assertThatThrownBy(() -> testService.testMethod(crewId, memberId))
                .isExactlyInstanceOf(CommonBusinessException.RequestConflict.class);
    }

    static class TestService {

        @Throttling(name = "test-request-cache", key = "'crew:' + #crewId + ':member:' + #memberId", during = 5)
        public void testMethod(Long crewId, Long memberId) {
            // ignored
        }
    }
}
