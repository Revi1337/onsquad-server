package revi1337.onsquad.squad_request.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestWithSquadDomainDto;

public record SquadRequestWithSquadDto(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberDto squadOwner,
        RequestParticipantDto request
) {

    public static SquadRequestWithSquadDto from(SquadRequestWithSquadDomainDto squadRequestWithSquadDomainDto) {
        return new SquadRequestWithSquadDto(
                squadRequestWithSquadDomainDto.id(),
                squadRequestWithSquadDomainDto.title().getValue(),
                squadRequestWithSquadDomainDto.capacity(),
                squadRequestWithSquadDomainDto.capacity(),
                squadRequestWithSquadDomainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberDto.from(squadRequestWithSquadDomainDto.squadOwner()),
                RequestParticipantDto.from(squadRequestWithSquadDomainDto.request())
        );
    }

    public record RequestParticipantDto(
            Long id,
            LocalDateTime requestAt
    ) {

        public static RequestParticipantDto from(
                SquadRequestWithSquadDomainDto.RequestParticipantDomainDto requestParticipantDomainDto) {
            return new RequestParticipantDto(
                    requestParticipantDomainDto.id(),
                    requestParticipantDomainDto.requestAt()
            );
        }
    }
}
