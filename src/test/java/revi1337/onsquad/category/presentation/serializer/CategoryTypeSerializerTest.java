package revi1337.onsquad.category.presentation.serializer;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;

class CategoryTypeSerializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SimpleModule().addSerializer(CategoryType.class, new CategoryTypeSerializer()));

    @Test
    @DisplayName("존재하는 CategoryType 은 직렬화에 성공한다.")
    void success() throws JsonProcessingException {
        CategoryType categoryType = CategoryType.ACTIVITY;

        String result = objectMapper.writeValueAsString(categoryType);

        assertThat(result).isEqualTo("\"" + categoryType.getText() + "\"");
    }
}
