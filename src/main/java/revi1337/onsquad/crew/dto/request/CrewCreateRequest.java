package revi1337.onsquad.crew.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.crew.dto.CrewDto;
import revi1337.onsquad.image.dto.ImageDto;
import revi1337.onsquad.member.dto.MemberDto;

import java.util.List;

public record CrewCreateRequest(
        @NotEmpty String name,
        @NotEmpty String introduce,
        @NotEmpty String detail,
        List<String> hashTags,
        String kakaoLink
) {
    public CrewDto toDto(MemberDto memberDto, byte[] imageData) {
        return CrewDto.of(
                name,
                introduce,
                hashTags,
                kakaoLink,
                ImageDto.builder()
                        .image(imageData)
                        .build(),
                memberDto
        );
    }
}
