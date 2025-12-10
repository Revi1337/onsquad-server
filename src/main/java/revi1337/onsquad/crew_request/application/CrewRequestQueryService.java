package revi1337.onsquad.crew_request.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_request.application.dto.CrewRequestWithCrewDto;
import revi1337.onsquad.crew_request.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrewRequestQueryService {

    private final CrewRequestRepository crewRequestRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<CrewRequestWithMemberDto> fetchAllRequests(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }

        return crewRequestRepository.fetchCrewRequests(crewId, pageable).stream()
                .map(CrewRequestWithMemberDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CrewRequestWithCrewDto> fetchAllCrewRequests(Long memberId) {
        return crewRequestRepository.fetchAllWithSimpleCrewByMemberId(memberId).stream()
                .map(CrewRequestWithCrewDto::from)
                .toList();
    }

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) {  // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }
}
