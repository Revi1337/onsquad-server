package revi1337.onsquad.image.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ImageDomainDto(
        String imageUrl
) {
    @QueryProjection
    public ImageDomainDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
