package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.domain.CrewResults;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew.domain.result.CrewWithOwnerStateResult;
import revi1337.onsquad.crew.error.CrewBusinessException;
import revi1337.onsquad.crew.error.CrewBusinessException.NotFound;
import revi1337.onsquad.crew.error.CrewErrorCode;

@RequiredArgsConstructor
@Component
public class CrewAccessor {

    private final CrewRepository crewRepository;

    public Crew getById(Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));
    }

    public Crew getByIdForUpdate(Long crewId) {
        return crewRepository.findByIdForUpdate(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));
    }

    public Crew getReferenceById(Long crewId) {
        return crewRepository.getReferenceById(crewId);
    }

    public CrewResult getCrewWithDetailById(Long crewId) {
        return crewRepository.fetchCrewWithDetailById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));
    }

    public CrewResult fetchCrewWithDetailById(Long crewId) {
        return crewRepository.fetchCrewWithDetailById(crewId)
                .orElseThrow(() -> new NotFound(CrewErrorCode.NOT_FOUND));
    }

    public CrewResults fetchCrewsWithDetailByName(String crewName, Pageable pageable) {
        return new CrewResults(crewRepository.fetchCrewsWithDetailByName(crewName, pageable));
    }

    public CrewResults fetchCrewsWithDetailByMemberId(Long memberId, Pageable pageable) {
        return new CrewResults(crewRepository.fetchCrewsWithDetailByMemberId(memberId, pageable));
    }

    public List<CrewWithOwnerStateResult> fetchCrewWithStateByIdsIn(List<Long> crewIds, Long currentMemberId) {
        return crewRepository.fetchCrewWithStateByIdsIn(crewIds, currentMemberId);
    }

    public boolean checkCrewNameExists(String name) {
        return crewRepository.existsByName(new Name(name));
    }

    public void validateCrewNameIsDuplicate(String name) {
        if (crewRepository.existsByName(new Name(name))) {
            throw new CrewBusinessException.AlreadyExists(CrewErrorCode.ALREADY_EXISTS);
        }
    }
}
