package revi1337.onsquad.crew.application;

import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.DuplicateResponse;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew.application.dto.response.CrewResponse;
import revi1337.onsquad.crew.application.dto.response.CrewWithParticipantStateResponse;
import revi1337.onsquad.crew.domain.model.CrewDetail;
import revi1337.onsquad.crew.domain.model.CrewDetails;
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

    public DuplicateResponse checkNameDuplicate(String name) {
        if (crewAccessor.checkCrewNameExists(name)) {
            return DuplicateResponse.of(true);
        }

        return DuplicateResponse.of(false);
    }

    public CrewWithParticipantStateResponse findCrewById(@Nullable Long memberId, Long crewId) {
        CrewDetail result = crewAccessor.fetchCrewWithDetailById(crewId);
        if (memberId == null) {
            return CrewWithParticipantStateResponse.from(null, result);
        }
        boolean alreadyParticipant = crewMemberAccessor.checkAlreadyParticipant(memberId, crewId);

        return CrewWithParticipantStateResponse.from(alreadyParticipant, result);
    }

    public PageResponse<CrewResponse> fetchCrewsByName(String crewName, Pageable pageable) {
        Page<CrewDetail> pageResults = crewAccessor.fetchCrewsWithDetailByName(crewName, pageable);
        CrewDetails results = new CrewDetails(pageResults.getContent());
        if (results.isNotEmpty()) {
            CrewHashtags hashtags = crewHashtagAccessor.fetchHashtagsByCrewIdIn(results.getIds());
            results.linkHashtags(hashtags);
        }

        List<CrewResponse> response = results.map(CrewResponse::from);
        return PageResponse.from(new PageImpl<>(response, pageResults.getPageable(), pageResults.getTotalElements()));
    }
}
