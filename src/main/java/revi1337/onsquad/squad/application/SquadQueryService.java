package revi1337.onsquad.squad.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoWithOwnerFlagDto;
import revi1337.onsquad.squad.application.dto.SquadInfoDto;
import revi1337.onsquad.squad.domain.SquadRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadQueryService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;

    public SquadInfoDto findSquad(Long memberId, Long crewId, Long squadId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        return SquadInfoDto.from(squadRepository.getSquadById(squadId));
    }

    public List<SquadInfoDto> findSquads(Long crewId, CategoryCondition condition, Pageable pageable) {
        return squadRepository.findSquadsByCrewId(crewId, condition.categoryType(), pageable).stream()
                .map(SquadInfoDto::from)
                .collect(Collectors.toList());
    }

    public List<SimpleSquadInfoWithOwnerFlagDto> fetchSquadsWithOwnerFlag(Long memberId, Long crewId,
                                                                          Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return squadRepository.fetchSquadsWithOwnerFlag(memberId, crewId, pageable).stream()
                .map(SimpleSquadInfoWithOwnerFlagDto::from)
                .toList();
    }
}
