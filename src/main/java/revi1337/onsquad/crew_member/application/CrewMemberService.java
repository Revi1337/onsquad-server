package revi1337.onsquad.crew_member.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;

    public Page<CrewMemberDto> fetchCrewMembers(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return crewMemberRepository.findManagedCrewMembersByCrewId(crewId, pageable)
                .map(CrewMemberDto::from);
    }
}
