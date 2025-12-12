package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.CrewDto;
import revi1337.onsquad.crew.application.dto.EnrolledCrewDto;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.dto.CrewWithParticipantStateDto;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
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
    private final CrewAccessPolicy crewAccessPolicy;

    public boolean isDuplicateCrewName(String name) {
        try {
            crewAccessPolicy.ensureCrewNameIsDuplicate(name);
            return true;
        } catch (CrewBusinessException e) {
            return false;
        }
    }

    public CrewDto findCrewById(Long crewId) {
        CrewDomainDto crewInfo = crewRepository.findCrewById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));
        return CrewDto.from(crewInfo);
    }

    public CrewWithParticipantStateDto findCrewById(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        boolean alreadyParticipants = crewMember != null;
        CrewDomainDto crewInfo = crewRepository.findCrewById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));
        return CrewWithParticipantStateDto.from(alreadyParticipants, crewInfo);
    }

    public List<CrewDto> fetchCrewsByName(String crewName, Pageable pageable) {
        return crewRepository.fetchCrewsByName(crewName, pageable).stream()
                .map(CrewDto::from)
                .toList();
    }

    public List<CrewDto> fetchOwnedCrews(Long memberId, Pageable pageable) {
        return crewRepository.fetchOwnedByMemberId(memberId, pageable).stream()
                .map(CrewDto::from)
                .toList();
    }

    public List<EnrolledCrewDto> fetchParticipantCrews(Long memberId) {
        return crewRepository.fetchParticipantsByMemberId(memberId).stream()
                .map(EnrolledCrewDto::from)
                .toList();
    }
}
