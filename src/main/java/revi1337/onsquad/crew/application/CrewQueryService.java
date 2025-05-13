package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.CrewInfoDto;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
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

    public CrewInfoDto findCrewById(Long crewId) {
        return CrewInfoDto.from(crewRepository.getCrewById(crewId));
    }

    public CrewWithParticipantStateDto findCrewById(Long memberId, Long crewId) {
        Boolean alreadyJoin = crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId);
        CrewInfoDomainDto crewInfo = crewRepository.getCrewById(crewId);

        return CrewWithParticipantStateDto.from(alreadyJoin, crewInfo);
    }

    public List<CrewInfoDto> fetchCrewsByName(String crewName, Pageable pageable) {
        return crewRepository.fetchCrewsByName(crewName, pageable).stream()
                .map(CrewInfoDto::from)
                .toList();
    }

    public List<EnrolledCrewDto> fetchMyParticipants(Long memberId) {
        return crewMemberRepository.fetchEnrolledCrewsByMemberId(memberId).stream()
                .map(EnrolledCrewDto::from)
                .toList();
    }
}
