package revi1337.onsquad.image.presentation.dto;

import revi1337.onsquad.image.application.dto.ImageDto;

public record ImageResponse(
        String imageUrl
) {
    public static ImageResponse from(ImageDto imageDto) {
        return new ImageResponse(imageDto.imageUrl());
    }
}
