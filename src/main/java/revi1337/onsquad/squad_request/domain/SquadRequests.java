package revi1337.onsquad.squad_request.domain;

import java.util.Collections;
import java.util.List;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public class SquadRequests {

    private final List<SquadRequest> requests;

    public SquadRequests(List<SquadRequest> requests) {
        this.requests = Collections.unmodifiableList(requests);
    }

    public List<Long> getSquadIds() {
        return requests.stream()
                .map(SquadRequest::getSquadId)
                .toList();
    }

    public List<SquadRequest> values() {
        return requests;
    }

    public boolean isNotEmpty() {
        return !requests.isEmpty();
    }
}
