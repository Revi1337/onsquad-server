package revi1337.onsquad.notification.infrastructure.discord;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DiscordMessage {

    private String username;
    private String avatarUrl;
    private String content;
    private String threadName;
    private List<Embed> embeds;
    private List<Attachment> attachments;

    @Builder(access = PUBLIC)
    private DiscordMessage(String username, String avatarUrl, String content, String threadName, List<Embed> embeds, List<Attachment> attachments) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.threadName = threadName;
        this.embeds = embeds;
        this.attachments = attachments;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor(access = PACKAGE)
    public static class Embed {

        public static final int COLOR_RED = 15548997;
        public static final int COLOR_GREEN = 4445074;
        public static final int COLOR_BLUE = 6261997;

        private String title;
        private String description;
        private String url;
        private Integer color;
        private List<Field> fields;
        private Author author;
        private Footer footer;
        private @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC") LocalDateTime timestamp;
        private Image image;
        private Thumbnail thumbnail;

        @Builder(access = PUBLIC)
        private Embed(String title, String description, String url, Integer color, List<Field> fields, Author author,
                      Footer footer, LocalDateTime timestamp, Image image, Thumbnail thumbnail) {
            this.title = title;
            this.description = description;
            this.url = url;
            this.color = color;
            this.fields = fields;
            this.author = author;
            this.footer = footer;
            this.timestamp = timestamp;
            this.image = image;
            this.thumbnail = thumbnail;
        }

        @Getter
        @AllArgsConstructor(access = PUBLIC)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Field {

            private String name;
            private String value;

        }

        @Getter
        @Builder(access = PUBLIC)
        @AllArgsConstructor(access = PRIVATE)
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Author {

            private String name;
            private String url;
            private String iconUrl;

        }

        @Getter
        @AllArgsConstructor(access = PUBLIC)
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Footer {

            private String text;
            private String iconUrl;

        }

        @Getter
        @AllArgsConstructor(access = PUBLIC)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Image {

            private String url;

        }

        @Getter
        @AllArgsConstructor(access = PUBLIC)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Thumbnail {

            private String url;

        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor(access = PRIVATE)
    public static class Attachment {

    }
}
