package revi1337.onsquad.crew.application;

import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.PageResponse;
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

    public PageResponse<CrewResponse> fetchCrewsByName(String crewName, Pageable pageable) {
        Page<CrewResult> pageResults = crewAccessor.fetchCrewsWithDetailByName(crewName, pageable);
        CrewResults results = new CrewResults(pageResults.getContent());
        if (results.isNotEmpty()) {
            CrewHashtags hashtags = crewHashtagAccessor.fetchHashtagsByCrewIdIn(results.getIds());
            results.linkHashtags(hashtags);
        }

        List<CrewResponse> response = results.map(CrewResponse::from);
        return PageResponse.from(new PageImpl<>(response, pageResults.getPageable(), pageResults.getTotalElements()));
    }
}
