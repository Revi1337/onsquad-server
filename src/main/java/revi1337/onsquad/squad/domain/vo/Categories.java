package revi1337.onsquad.squad.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Categories {

    private static final String CATEGORY_DELIMITER = ",";
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 5;

    @Column(name = "categories")
    private String value;

    public Categories(Category... categories) {
        validateSize(categories);
        this.value = Arrays.stream(categories)
                .map(Category::getText)
                .collect(Collectors.joining(CATEGORY_DELIMITER));
    }

    public Categories(String... categories) {
        validate(categories);
        this.value = String.join(CATEGORY_DELIMITER, new HashSet<>(Arrays.asList(categories)));
    }

    public void validate(String... categories) {
        validateSize(categories);
        for (String category : categories) {
            if (!Category.contains(category)) {
                throw new IllegalArgumentException("일치하지 않는 카테고리가 존재합니다."); // TODO 커스텀 익셉션 필요.
            }
        }
    }

    public void validateSize(Object... categories) {
        HashSet<Object> categoryHashSet = new HashSet<>(Arrays.asList(categories));
        if (categoryHashSet.isEmpty() || categoryHashSet.size() > MAX_LENGTH) {
            throw new IllegalArgumentException( // TODO 커스텀 익셉션 필요.
                    String.format("카테고리는 %d ~ %d 개수여야 합니다.", MIN_LENGTH, MAX_LENGTH)
            );
        }
    }
}
