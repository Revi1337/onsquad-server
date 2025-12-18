package revi1337.onsquad.history.config;

import java.util.Set;
import revi1337.onsquad.crew.application.CrewCommandService;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew_request.application.CrewRequestCommandService;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad_request.application.SquadRequestCommandService;

public record HistoricCandidate(Class<?> clazz, String method, Class<?>[] paramTypes) {

    public static final Set<HistoricCandidate> CANDIDATES = Set.of(
            new HistoricCandidate(CrewCommandService.class, "newCrew", new Class<?>[]{Long.class, CrewCreateDto.class, String.class}),
            new HistoricCandidate(CrewRequestCommandService.class, "cancelMyRequest", new Class<?>[]{Long.class, Long.class}),

            new HistoricCandidate(SquadCommandService.class, "newSquad", new Class<?>[]{Long.class, Long.class, SquadCreateDto.class}),
            new HistoricCandidate(SquadRequestCommandService.class, "cancelMyRequest", new Class<?>[]{Long.class, Long.class})
    );
}
