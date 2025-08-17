package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;

public record SquadDto(
        Long id,
        String title,
        String content,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        List<String> categories,
        SimpleMemberDto leader
) {
    public static SquadDto from(SquadDomainDto squadDomainDto) {
        return new SquadDto(
                squadDomainDto.id(),
                squadDomainDto.title().getValue(),
                squadDomainDto.content().getValue(),
                squadDomainDto.capacity(),
                squadDomainDto.remain(),
                squadDomainDto.address().getValue(),
                squadDomainDto.address().getDetail(),
                squadDomainDto.kakaoLink(),
                squadDomainDto.discordLink(),
                squadDomainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberDto.from(squadDomainDto.leader())
        );
    }
}
