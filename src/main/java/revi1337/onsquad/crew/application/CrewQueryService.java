package revi1337.onsquad.crew.application;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.response.CrewResponse;
import revi1337.onsquad.crew.application.dto.response.CrewWithParticipantStateResponse;
import revi1337.onsquad.crew.application.dto.response.DuplicateCrewNameResponse;
import revi1337.onsquad.crew.domain.CrewResults;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException.NotFound;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtags;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewQueryService {

    private final CrewRepository crewRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final CrewMemberAccessPolicy crewMemberAccessPolicy;

    public DuplicateCrewNameResponse checkNameDuplicate(String name) {
        if (crewRepository.existsByName(new Name(name))) {
            return DuplicateCrewNameResponse.of(true);
        }

        return DuplicateCrewNameResponse.of(false);
    }

    public CrewWithParticipantStateResponse findCrewById(@Nullable Long memberId, Long crewId) {
        CrewResult result = crewRepository.fetchCrewWithDetailById(crewId).orElseThrow(() -> new NotFound(CrewErrorCode.NOT_FOUND));
        if (memberId == null) {
            return CrewWithParticipantStateResponse.from(null, result);
        }
        boolean alreadyParticipant = crewMemberAccessPolicy.alreadyParticipant(memberId, crewId);

        return CrewWithParticipantStateResponse.from(alreadyParticipant, result);
    }

    public List<CrewResponse> fetchCrewsByName(String crewName, Pageable pageable) {
        CrewResults results = new CrewResults(crewRepository.fetchCrewsWithDetailByName(crewName, pageable));
        if (results.isNotEmpty()) {
            CrewHashtags hashtags = new CrewHashtags(crewHashtagRepository.fetchHashtagsByCrewIdIn(results.getIds()));
            linkHashtags(results, hashtags);
        }

        return results.values().stream()
                .map(CrewResponse::from)
                .toList();
    }

    public List<CrewResponse> fetchOwnedCrews(Long memberId, Pageable pageable) {
        CrewResults results = new CrewResults(crewRepository.fetchCrewsWithDetailByMemberId(memberId, pageable));
        if (results.isNotEmpty()) {
            CrewHashtags hashtags = new CrewHashtags(crewHashtagRepository.fetchHashtagsByCrewIdIn(results.getIds()));
            linkHashtags(results, hashtags);
        }

        return results.values().stream()
                .map(CrewResponse::from)
                .toList();
    }

    private void linkHashtags(CrewResults crews, CrewHashtags hashtags) {
        List<CrewResult> results = crews.values();
        Map<Long, List<HashtagType>> hashtagMap = hashtags.groupByCrewId();
        results.forEach(result -> {
            List<HashtagType> hashtagTypes = hashtagMap.get(result.getId());
            if (hashtagTypes != null) {
                result.addHashtagTypes(hashtagTypes);
            }
        });
    }
}
