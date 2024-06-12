package revi1337.onsquad.squad.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@ToString(of = "value")
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Categories {

    private static final String CATEGORY_DELIMITER = ",";
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 5;

    @Getter
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
        this.value = String.join(CATEGORY_DELIMITER, categories);
    }

    public void validate(String... categories) {
        validateSize(categories);
        for (String category : categories) {
            if (!Category.contains(category)) {
                throw new IllegalArgumentException("일치하지 않는 카테고리가 존재합니다.");
            }
        }
    }

    public void validateSize(Object... categories) {
        HashSet<Object> categoryHashSet = new HashSet<>(Arrays.asList(categories));
        if (categoryHashSet.isEmpty() || categoryHashSet.size() > MAX_LENGTH) {
            throw new IllegalArgumentException("너무 많은 카테고리가 포함되어 있습니다.");
        }
    }
}
