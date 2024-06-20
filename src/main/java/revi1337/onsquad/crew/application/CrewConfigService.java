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
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;

import java.util.List;

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
                                throw new IllegalArgumentException("Crew 를 생성한 사용자와 현재 사용자가 일치하지 않습니다"); // TODO 커스텀 익셉션 필요

                            crew.updateCrew(dto.name(), dto.introduce(), dto.detail(), dto.hashTags(), dto.kakaoLink(), image);
                            crewRepository.saveAndFlush(crew);
                        },
                        () -> { throw new IllegalArgumentException("Crew 를 찾을 수 없습니다."); } // TODO 커스텀 익셉션 필요
                );
    }

    @Transactional
    public void acceptCrewMember(CrewAcceptDto dto, Long memberId) {
        crewRepository.findByName(new Name(dto.requestCrewName()))
                .ifPresentOrElse(
                        crew -> updateCrewMemberMetadata(dto, memberId, crew),
                        () -> { throw new IllegalArgumentException("Crew 를 찾을 수 없습니다."); } // TODO 커스텀 익셉션 필요
                );
    }

    private void updateCrewMemberMetadata(CrewAcceptDto dto, Long memberId, Crew crew) {
        if (!crew.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("현재 사용자는 수락하려는 Crew 를 생성한 이력이 없습니다."); // TODO 커스텀 익셉션 필요
        }

        updateCrewMemberStatus(dto.requestStatus(), memberId, crew.getId());
    }

    private void updateCrewMemberStatus(JoinStatus requestJoinStatus, Long memberId, Long crewId) {
        crewMemberRepository.findCrewMemberByCrewIdAndMemberId(crewId, memberId)
                .ifPresentOrElse(
                        crewMember -> updateStatus(requestJoinStatus, crewMember),
                        () -> { throw new IllegalArgumentException("사용자는 해당 Crew 에 참여요청을 한 이력이 없습니다."); } // TODO 커스텀 익셉션 필요
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
