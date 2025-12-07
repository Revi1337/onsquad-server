package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.squad_comment.application.policy.DefaultCommentMaskPolicy;
import revi1337.onsquad.squad_comment.application.strategy.IterativeTwoLevelCommentSanitizer;
import revi1337.onsquad.squad_comment.application.strategy.RecursiveCommentSanitizer;
import revi1337.onsquad.squad_comment.application.strategy.StackBasedDfsCommentSanitizer;

@ContextConfiguration(classes = {
        DefaultCommentMaskPolicy.class,
        RecursiveCommentSanitizer.class,
        IterativeTwoLevelCommentSanitizer.class,
        StackBasedDfsCommentSanitizer.class,
        CommentSanitizer.class
})
@ExtendWith(SpringExtension.class)
class CommentSanitizerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("CommentSanitizer 가 갖고있는 전략이 Stack Based DFS 전략인지 확인한다.")
    void success() {
        CommentSanitizer commentSanitizer = applicationContext.getBean(CommentSanitizer.class);

        Object injectedStrategy = ReflectionTestUtils.getField(commentSanitizer, "sanitizeStrategy");

        assertThat(injectedStrategy).isInstanceOf(StackBasedDfsCommentSanitizer.class);
    }
}
