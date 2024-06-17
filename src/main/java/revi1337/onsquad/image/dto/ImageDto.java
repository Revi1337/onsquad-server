package revi1337.onsquad.image.dto;

import lombok.Builder;
import lombok.Getter;
import revi1337.onsquad.image.domain.Image;

@Getter
public class ImageDto {

    private Long id;
    private byte[] image;

    @Builder
    private ImageDto(Long id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    public Image toEntity() {
        return new Image(image);
    }

    public static ImageDto from(Image image) {
        return new ImageDto(image.getId(), image.getData());
    }
}
