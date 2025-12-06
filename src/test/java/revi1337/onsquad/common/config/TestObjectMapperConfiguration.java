package revi1337.onsquad.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.category.presentation.deserializer.CategoryTypeDeserializer;
import revi1337.onsquad.category.presentation.serializer.CategoryTypeSerializer;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;
import revi1337.onsquad.hashtag.presentation.deserializer.HashtagTypeDeserializer;
import revi1337.onsquad.hashtag.presentation.serializer.HashtagTypeSerializer;

@TestConfiguration
public class TestObjectMapperConfiguration {

    @Primary
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build()
                .registerModule(new JavaTimeModule())
                .registerModule(new SimpleModule().addDeserializer(HashtagType.class, new HashtagTypeDeserializer()))
                .registerModule(new SimpleModule().addSerializer(HashtagType.class, new HashtagTypeSerializer()))
                .registerModule(new SimpleModule().addDeserializer(CategoryType.class, new CategoryTypeDeserializer()))
                .registerModule(new SimpleModule().addSerializer(CategoryType.class, new CategoryTypeSerializer()))
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
