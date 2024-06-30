package revi1337.onsquad.image.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    public Image(String imageUrl) {
        validate(imageUrl);
        this.imageUrl = imageUrl;
    }

    private void validate(String imageUrl) {
        if (imageUrl == null) {
            throw new NullPointerException("크루 image url 은 null 일 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image data)) return false;
        return id != null && Objects.equals(getId(), data.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Image updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}
