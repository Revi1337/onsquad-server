package revi1337.onsquad.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import revi1337.onsquad.category.presentation.converter.HashtagTypeDeserializer;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;

@Configuration
public class ObjectMapperConfig {

    @Primary
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build()
                .registerModule(new JavaTimeModule())
                .registerModule(new SimpleModule().addDeserializer(HashtagType.class, new HashtagTypeDeserializer()))
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
