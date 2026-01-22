package revi1337.onsquad.crew.domain.event;

import java.util.List;
import lombok.Getter;

@Getter
public class CrewContextDisposed {

    private final List<Long> deletedCrewIds;
    private final List<String> crewImageUrls;

    public CrewContextDisposed(Long deletedCrewId, List<String> crewImageUrls) {
        this(List.of(deletedCrewId), crewImageUrls);
    }

    public CrewContextDisposed(List<Long> deletedCrewIds, List<String> crewImageUrls) {
        this.deletedCrewIds = deletedCrewIds;
        this.crewImageUrls = crewImageUrls;
    }
}
