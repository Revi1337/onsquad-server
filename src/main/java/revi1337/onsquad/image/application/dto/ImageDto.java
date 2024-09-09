package revi1337.onsquad.image.application.dto;

import revi1337.onsquad.image.domain.dto.ImageDomainDto;

public record ImageDto(
        String imageUrl
) {
    public static ImageDto from(ImageDomainDto imageDomainDto) {
        return new ImageDto(imageDomainDto.imageUrl());
    }
}
