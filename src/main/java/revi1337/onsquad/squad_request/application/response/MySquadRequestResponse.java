package revi1337.onsquad.squad_request.application.response;

import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.application.response.SimpleSquadResponse;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public record MySquadRequestResponse(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleSquadResponse squad,
        RequestResponse request
) {

    public static MySquadRequestResponse from(SquadRequest squadRequest) {
        return new MySquadRequestResponse(
                squadRequest.getSquad().getCrew().getId(),
                squadRequest.getSquad().getCrew().getName().getValue(),
                squadRequest.getSquad().getCrew().getImageUrl() != null ? squadRequest.getSquad().getCrew().getImageUrl() : "",
                SimpleSquadResponse.from(squadRequest.getSquad()),
                RequestResponse.from(squadRequest)
        );
    }

    public static MySquadRequestResponse from(SquadRequest squadRequest, List<CategoryType> categories) {
        return new MySquadRequestResponse(
                squadRequest.getSquad().getCrew().getId(),
                squadRequest.getSquad().getCrew().getName().getValue(),
                squadRequest.getSquad().getCrew().getImageUrl() != null ? squadRequest.getSquad().getCrew().getImageUrl() : "",
                SimpleSquadResponse.from(squadRequest.getSquad(), categories),
                RequestResponse.from(squadRequest)
        );
    }
}
