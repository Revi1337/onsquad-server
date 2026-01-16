package revi1337.onsquad.crew.application;

import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.response.CrewResponse;
import revi1337.onsquad.crew.application.dto.response.CrewWithParticipantStateResponse;
import revi1337.onsquad.crew.application.dto.response.DuplicateCrewNameResponse;
import revi1337.onsquad.crew.domain.CrewResults;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew_hashtag.application.CrewHashtagAccessor;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtags;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewQueryService {

    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewHashtagAccessor crewHashtagAccessor;

    public DuplicateCrewNameResponse checkNameDuplicate(String name) {
        if (crewAccessor.checkCrewNameExists(name)) {
            return DuplicateCrewNameResponse.of(true);
        }

        return DuplicateCrewNameResponse.of(false);
    }

    public CrewWithParticipantStateResponse findCrewById(@Nullable Long memberId, Long crewId) {
        CrewResult result = crewAccessor.fetchCrewWithDetailById(crewId);
        if (memberId == null) {
            return CrewWithParticipantStateResponse.from(null, result);
        }
        boolean alreadyParticipant = crewMemberAccessor.checkAlreadyParticipant(memberId, crewId);

        return CrewWithParticipantStateResponse.from(alreadyParticipant, result);
    }

    public List<CrewResponse> fetchCrewsByName(String crewName, Pageable pageable) {
        CrewResults results = crewAccessor.fetchCrewsWithDetailByName(crewName, pageable);
        if (results.isNotEmpty()) {
            CrewHashtags hashtags = crewHashtagAccessor.fetchHashtagsByCrewIdIn(results.getIds());
            results.linkHashtags(hashtags);
        }

        return results.values().stream()
                .map(CrewResponse::from)
                .toList();
    }

    public List<CrewResponse> fetchOwnedCrews(Long memberId, Pageable pageable) {
        CrewResults results = crewAccessor.fetchCrewsWithDetailByMemberId(memberId, pageable);
        if (results.isNotEmpty()) {
            CrewHashtags hashtags = crewHashtagAccessor.fetchHashtagsByCrewIdIn(results.getIds());
            results.linkHashtags(hashtags);
        }

        return results.values().stream()
                .map(CrewResponse::from)
                .toList();
    }
}
