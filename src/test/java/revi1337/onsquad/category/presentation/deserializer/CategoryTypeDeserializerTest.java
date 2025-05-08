package revi1337.onsquad.category.presentation.deserializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.error.exception.SquadDomainException;

class CategoryTypeDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SimpleModule().addDeserializer(CategoryType.class, new CategoryTypeDeserializer()));

    @Test
    @DisplayName("존재하는 CategoryType 은 역직렬화에 성공한다.")
    void success() throws JsonProcessingException {
        String json = "\"게임\"";

        CategoryType result = objectMapper.readValue(json, CategoryType.class);

        assertThat(result).isSameAs(CategoryType.GAME);
    }

    @Test
    @DisplayName("존재하지 않는 CategoryType 는 역직렬화에 실패한다.")
    void fail1() {
        String json = "\"INVALID_CATEGORY_TYPE\"";

        assertThatThrownBy(() -> objectMapper.readValue(json, CategoryType.class))
                .isExactlyInstanceOf(SquadDomainException.InvalidCategory.class);
    }
}
