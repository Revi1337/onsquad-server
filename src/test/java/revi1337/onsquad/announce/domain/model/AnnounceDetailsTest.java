package revi1337.onsquad.announce.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.announce.domain.entity.vo.Title;
import revi1337.onsquad.member.domain.entity.vo.Introduce;
import revi1337.onsquad.member.domain.entity.vo.Mbti;
import revi1337.onsquad.member.domain.entity.vo.Nickname;
import revi1337.onsquad.member.domain.model.SimpleMember;

class AnnounceDetailsTest {

    @Test
    @DisplayName("공지사항 목록에서 중복을 제거한 작성자 ID 리스트를 추출한다.")
    void getWriterIds() {
        SimpleMember writer1 = createSimpleMember(1L, "작성자1");
        SimpleMember writer2 = createSimpleMember(2L, "작성자2");
        AnnounceDetails announceDetails = new AnnounceDetails(List.of(
                createAnnounceDetail(101L, writer1),
                createAnnounceDetail(102L, writer1),
                createAnnounceDetail(103L, writer2)
        ));

        List<Long> writerIds = announceDetails.getWriterIds();

        assertThat(writerIds).hasSize(2).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("내부 리스트를 반환할 때 원본 리스트의 순서를 유지한다.")
    void values() {
        AnnounceDetail detail1 = createAnnounceDetail(101L, createSimpleMember(1L, "작성자1"));
        AnnounceDetail detail2 = createAnnounceDetail(102L, createSimpleMember(2L, "작성자2"));
        AnnounceDetails announceDetails = new AnnounceDetails(List.of(detail1, detail2));

        List<AnnounceDetail> values = announceDetails.values();

        assertThat(values).hasSize(2);
        assertThat(values.get(0).id()).isEqualTo(101L);
        assertThat(values.get(1).id()).isEqualTo(102L);
    }

    @Test
    @DisplayName("반환된 리스트를 수정하려고 하면 예외가 발생한다 (불변성 검증).")
    void immutableValues() {
        AnnounceDetails announceDetails = new AnnounceDetails(new ArrayList<>(List.of(
                createAnnounceDetail(101L, createSimpleMember(1L, "작성자1"))
        )));

        List<AnnounceDetail> values = announceDetails.values();
        assertThatThrownBy(() -> values.add(createAnnounceDetail(102L, createSimpleMember(2L, "작성자2"))))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private AnnounceDetail createAnnounceDetail(Long id, SimpleMember writer) {
        return new AnnounceDetail(
                id,
                new Title("테스트 제목"),
                "테스트 내용",
                LocalDateTime.now(),
                false,
                null,
                writer
        );
    }

    private SimpleMember createSimpleMember(Long id, String nickname) {
        return new SimpleMember(id, new Nickname(nickname), new Introduce("안녕"), Mbti.ENTJ);
    }
}
