package revi1337.onsquad.squad_participant.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantDomainDto;

public record SquadParticipantDto(
        Long id,
        String title,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        List<String> categories,
        SimpleMemberInfoDto squadOwner,
        RequestParticipantDto request
) {
    public static SquadParticipantDto from(SquadParticipantDomainDto squadParticipantDomainDto) {
        return new SquadParticipantDto(
                squadParticipantDomainDto.id(),
                squadParticipantDomainDto.title().getValue(),
                squadParticipantDomainDto.capacity().getValue(),
                squadParticipantDomainDto.capacity().getRemain(),
                squadParticipantDomainDto.address().getValue(),
                squadParticipantDomainDto.address().getDetail(),
                squadParticipantDomainDto.kakaoLink(),
                squadParticipantDomainDto.discordLink(),
                squadParticipantDomainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberInfoDto.from(squadParticipantDomainDto.squadOwner()),
                RequestParticipantDto.from(squadParticipantDomainDto.request())
        );
    }

    public record RequestParticipantDto(
            Long id,
            LocalDateTime requestAt
    ) {
        public static RequestParticipantDto from(
                SquadParticipantDomainDto.RequestParticipantDomainDto requestParticipantDomainDto) {
            return new RequestParticipantDto(
                    requestParticipantDomainDto.id(),
                    requestParticipantDomainDto.requestAt()
            );
        }
    }
}
