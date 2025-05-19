package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.CrewDto;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.dto.CrewWithParticipantStateDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewQueryService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public boolean isDuplicateCrewName(String crewName) {
        return crewRepository.existsByName(new Name(crewName));
    }

    public CrewDto findCrewById(Long crewId) {
        return CrewDto.from(crewRepository.getCrewById(crewId));
    }

    public CrewWithParticipantStateDto findCrewById(Long memberId, Long crewId) {
        Boolean alreadyJoin = crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId);
        CrewDomainDto crewInfo = crewRepository.getCrewById(crewId);

        return CrewWithParticipantStateDto.from(alreadyJoin, crewInfo);
    }

    public List<CrewDto> fetchCrewsByName(String crewName, Pageable pageable) {
        return crewRepository.fetchCrewsByName(crewName, pageable).stream()
                .map(CrewDto::from)
                .toList();
    }

    public List<EnrolledCrewDto> fetchMyParticipants(Long memberId) {
        return crewMemberRepository.fetchEnrolledCrewsByMemberId(memberId).stream()
                .map(EnrolledCrewDto::from)
                .toList();
    }
}
