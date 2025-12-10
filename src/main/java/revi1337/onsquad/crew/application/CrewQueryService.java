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
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

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
        CrewDomainDto crewInfo = crewRepository.findCrewById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));
        return CrewDto.from(crewInfo);
    }

    public CrewWithParticipantStateDto findCrewById(Long memberId, Long crewId) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
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

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }
}
