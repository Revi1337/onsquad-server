package revi1337.onsquad.squad_participant.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantDomainDto;

public record SquadParticipantDto(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberDto squadOwner,
        RequestParticipantDto request
) {

    public static SquadParticipantDto from(SquadParticipantDomainDto squadParticipantDomainDto) {
        return new SquadParticipantDto(
                squadParticipantDomainDto.id(),
                squadParticipantDomainDto.title().getValue(),
                squadParticipantDomainDto.capacity(),
                squadParticipantDomainDto.capacity(),
                squadParticipantDomainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberDto.from(squadParticipantDomainDto.squadOwner()),
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
