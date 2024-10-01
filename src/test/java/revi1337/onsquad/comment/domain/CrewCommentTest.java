package revi1337.onsquad.comment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.comment.error.exception.CommentDomainException;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.Member;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("Comment 엔티티 테스트")
class CrewCommentTest {
    
    @Test
    @DisplayName("댓글이 비어있으면 실패한다.")
    public void commentTest1() {
        // given
        String content = "";
        
        // when
        assertThatThrownBy(() -> new Comment(content))
                .isInstanceOf(CommentDomainException.InvalidLength.class)
                .hasMessage("댓글은 비어있거나 250 자를 넘을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글이 250 자를 넘어가면 실패한다.")
    public void commentTest2() {
        // given
        String content = IntStream.of(new Random().ints(251, 67, 90).toArray())
                .mapToObj(integer -> (char) integer)
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        // when
        assertThatThrownBy(() -> new Comment(content))
                .isInstanceOf(CommentDomainException.InvalidLength.class)
                .hasMessage("댓글은 비어있거나 250 자를 넘을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글이 250 자 이하면 성공한다.")
    public void commentTest3() {
        // given
        String content = IntStream.of(new Random().ints(250, 67, 90).toArray())
                .mapToObj(integer -> (char) integer)
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        // when
        assertThat(new Comment(content).getContent()).hasSize(250);
    }
    
    @Test
    @DisplayName("Crew 를 포함한 Comment 가 생성되면 성공한다.")
    public void commentWithCrew() {
        // given
        String content = "comment content";
        Member member = MemberFactory.defaultMember().build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();

        // when
        Comment comment = Comment.forCrew(content, crew, member);
        
        // then
        assertSoftly(softly -> {
            softly.assertThat(comment.getCrew().getIntroduce()).isEqualTo(CrewFactory.INTRODUCE);
            softly.assertThat(comment.getCrew().getDetail()).isEqualTo(CrewFactory.DETAIL);
            softly.assertThat(comment.getCrew().getHashTags()).isEqualTo(CrewFactory.HASHTAGS);
        });
    }
}