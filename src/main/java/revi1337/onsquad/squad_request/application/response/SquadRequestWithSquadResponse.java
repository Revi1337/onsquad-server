package revi1337.onsquad.squad_request.application.response;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_request.domain.result.SquadRequestWithSquadResult;

public record SquadRequestWithSquadResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberDto squadOwner,
        RequestParticipantDto request
) {

    public static SquadRequestWithSquadResponse from(SquadRequestWithSquadResult squadRequestWithSquadResult) {
        return new SquadRequestWithSquadResponse(
                squadRequestWithSquadResult.id(),
                squadRequestWithSquadResult.title().getValue(),
                squadRequestWithSquadResult.capacity(),
                squadRequestWithSquadResult.capacity(),
                squadRequestWithSquadResult.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberDto.from(squadRequestWithSquadResult.squadOwner()),
                RequestParticipantDto.from(squadRequestWithSquadResult.request())
        );
    }

    public record RequestParticipantDto(
            Long id,
            LocalDateTime requestAt
    ) {

        public static RequestParticipantDto from(
                SquadRequestWithSquadResult.RequestParticipantDomainDto requestParticipantDomainDto) {
            return new RequestParticipantDto(
                    requestParticipantDomainDto.id(),
                    requestParticipantDomainDto.requestAt()
            );
        }
    }
}
