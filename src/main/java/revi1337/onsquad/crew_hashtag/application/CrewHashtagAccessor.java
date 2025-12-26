package revi1337.onsquad.crew_hashtag.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtags;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;

@RequiredArgsConstructor
@Component
public class CrewHashtagAccessor {

    private final CrewHashtagRepository crewHashtagRepository;

    public CrewHashtags fetchHashtagsByCrewIdIn(List<Long> crewIds) {
        return new CrewHashtags(crewHashtagRepository.fetchHashtagsByCrewIdIn(crewIds));
    }
}
