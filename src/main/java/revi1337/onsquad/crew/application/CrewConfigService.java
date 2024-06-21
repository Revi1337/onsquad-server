package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewAcceptDto;
import revi1337.onsquad.crew.dto.CrewUpdateDto;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

import java.util.List;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<OwnedCrewsDto> findOwnedCrews(Long memberId) {
        return crewRepository.findOwnedCrews(memberId)
                .stream()
                .toList();
    }

    public List<EnrolledCrewMemberDto> findMembersForSpecifiedCrew(String crewName, Long memberId) {
        return crewMemberRepository.findMembersForSpecifiedCrew(new Name(crewName), memberId);
    }

    @Transactional
    public void updateCrew(CrewUpdateDto dto, Long memberId, byte[] image) {
        crewRepository.findCrewByNameForUpdate(new Name(dto.name()))
                .ifPresentOrElse(
                        crew -> {
                            if (!crew.getMember().getId().equals(memberId))
                                throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, dto.name());

                            crew.updateCrew(dto.name(), dto.introduce(), dto.detail(), dto.hashTags(), dto.kakaoLink(), image);
                            crewRepository.saveAndFlush(crew);
                        },
                        () -> { throw new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, dto.name()); }
                );
    }

    @Transactional
    public void acceptCrewMember(CrewAcceptDto dto, Long memberId) {
        crewRepository.findByName(new Name(dto.requestCrewName()))
                .ifPresentOrElse(
                        crew -> updateCrewMemberMetadata(dto, memberId, crew),
                        () -> { throw new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, dto.requestCrewName()); }
                );
    }

    private void updateCrewMemberMetadata(CrewAcceptDto dto, Long memberId, Crew crew) {
        if (!crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, dto.requestCrewName());
        }

        updateCrewMemberStatus(dto, memberId, crew.getId());
    }

    private void updateCrewMemberStatus(CrewAcceptDto dto, Long memberId, Long crewId) {
        crewMemberRepository.findCrewMemberByCrewIdAndMemberId(crewId, memberId)
                .ifPresentOrElse(
                        crewMember -> updateStatus(dto.requestStatus(), crewMember),
                        () -> { throw new CrewMemberBusinessException.NeverRequested(NEVER_REQUESTED, dto.requestCrewName()); }
                );
    }

    private void updateStatus(JoinStatus requestJoinStatus, CrewMember crewMember) {
        if (requestJoinStatus == JoinStatus.PENDING) {
            return;
        }

        if (requestJoinStatus == JoinStatus.REJECT) {
            crewMemberRepository.delete(crewMember);
            return;
        }

        crewMember.updateStatus(requestJoinStatus);
    }
}
