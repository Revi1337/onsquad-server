package revi1337.onsquad.crew.domain.event;

import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import revi1337.onsquad.announce.domain.result.AnnounceReference;

@Getter
public class CrewContextDisposed {

    private final List<Long> deletedCrewIds;
    private final List<String> crewImageUrls;
    private final List<AnnounceReference> announceReferences;

    private CrewContextDisposed(List<Long> deletedCrewIds, List<String> crewImageUrls, List<AnnounceReference> announceReferences) {
        this.deletedCrewIds = deletedCrewIds;
        this.crewImageUrls = crewImageUrls;
        this.announceReferences = announceReferences;
    }

    public static CrewContextDisposed of(List<Long> deletedCrewIds, List<String> crewImageUrls, List<AnnounceReference> announceReferences) {
        return new CrewContextDisposed(deletedCrewIds, crewImageUrls, announceReferences);
    }

    public static CrewContextDisposed of(Long crewId, @Nullable String crewImage) {
        List<String> crewImages = crewImage == null ? List.of() : List.of(crewImage);
        return new CrewContextDisposed(List.of(crewId), crewImages, List.of());
    }
}
