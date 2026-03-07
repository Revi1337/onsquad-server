package revi1337.onsquad.infrastructure.aws.s3.cleanup.model;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilePathsTest {

    @Test
    @DisplayName("리스트가 비어있는지 확인한다")
    void checkEmpty() {
        FilePaths emptyFiles = new FilePaths(List.of());
        FilePaths existFiles = new FilePaths(createSampleFiles(1));

        assertSoftly(softly -> {
            softly.assertThat(emptyFiles.isEmpty()).isTrue();
            softly.assertThat(emptyFiles.isNotEmpty()).isFalse();
            softly.assertThat(existFiles.isEmpty()).isFalse();
            softly.assertThat(existFiles.isNotEmpty()).isTrue();
        });
    }

    @Test
    @DisplayName("지정한 사이즈 단위로 리스트를 분할(Partition)한다")
    void partition() {
        FilePaths targets = new FilePaths(createSampleFiles(10));

        List<FilePaths> partitions = targets.partition(3);

        assertSoftly(softly -> {
            softly.assertThat(partitions).hasSize(4);
            softly.assertThat(partitions.get(0).size()).isEqualTo(3);
            softly.assertThat(partitions.get(3).size()).isEqualTo(1);
            softly.assertThat(partitions.get(0).pathValues())
                    .containsExactly("path/file-1.png", "path/file-2.png", "path/file-3.png");
        });
    }

    @Test
    @DisplayName("경로 리스트와 일치하는 파일들만 필터링한다")
    void filterByPaths() {
        FilePaths targets = new FilePaths(createSampleFiles(5));
        List<String> searchPaths = List.of("path/file-1.png", "path/file-3.png", "non-exist.png");

        FilePaths filtered = targets.filterByPaths(searchPaths);

        assertSoftly(softly -> {
            softly.assertThat(filtered.size()).isEqualTo(2);
            softly.assertThat(filtered.getFileIds()).containsExactlyInAnyOrder(1L, 3L);
        });
    }

    @Test
    @DisplayName("ID 리스트와 경로(String) 리스트를 정확히 추출한다")
    void extractValues() {
        List<FilePath> rawList = List.of(
                new FilePath(10L, "a.jpg", 0),
                new FilePath(20L, "b.jpg", 0)
        );
        FilePaths targets = new FilePaths(rawList);

        assertSoftly(softly -> {
            softly.assertThat(targets.getFileIds()).containsExactly(10L, 20L);
            softly.assertThat(targets.pathValues()).containsExactly("a.jpg", "b.jpg");
        });
    }

    private List<FilePath> createSampleFiles(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new FilePath((long) i, "path/file-" + i + ".png", 0))
                .collect(Collectors.toList());
    }
}
