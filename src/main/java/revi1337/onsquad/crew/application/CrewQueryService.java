package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.response.CrewResponse;
import revi1337.onsquad.crew.application.dto.response.DuplicateCrewNameResponse;
import revi1337.onsquad.crew.application.dto.response.EnrolledCrewResponse;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew.domain.result.CrewWithParticipantResult;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewQueryService {

    private final CrewRepository crewRepository;
    private final CrewMemberAccessPolicy crewMemberAccessPolicy;

    public DuplicateCrewNameResponse checkNameDuplicate(String name) {
        if (crewRepository.existsByName(new Name(name))) {
            return DuplicateCrewNameResponse.of(true);
        }

        return DuplicateCrewNameResponse.of(false);
    }

    public CrewResponse findCrewById(Long crewId) {
        CrewResult crewInfo = crewRepository.findCrewById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));

        return CrewResponse.from(crewInfo);
    }

    public CrewWithParticipantResult findCrewById(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        boolean alreadyParticipants = crewMember != null;
        CrewResult crewInfo = crewRepository.findCrewById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));

        return CrewWithParticipantResult.from(alreadyParticipants, crewInfo);
    }

    public List<CrewResponse> fetchCrewsByName(String crewName, Pageable pageable) {
        return crewRepository.fetchCrewsByName(crewName, pageable).stream()
                .map(CrewResponse::from)
                .toList();
    }

    public List<CrewResponse> fetchOwnedCrews(Long memberId, Pageable pageable) {
        return crewRepository.fetchOwnedByMemberId(memberId, pageable).stream()
                .map(CrewResponse::from)
                .toList();
    }

    public List<EnrolledCrewResponse> fetchParticipantCrews(Long memberId) {
        return crewRepository.fetchParticipantsByMemberId(memberId).stream()
                .map(EnrolledCrewResponse::from)
                .toList();
    }
}
