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
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;

import java.util.List;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3BucketUploader s3BucketUploader;

    public List<OwnedCrewsDto> findOwnedCrews(Long memberId) {
        return crewRepository.findOwnedCrews(memberId)
                .stream()
                .toList();
    }

    public List<EnrolledCrewMemberDto> findMembersForSpecifiedCrew(String crewName, Long memberId) {
        return crewMemberRepository.findMembersForSpecifiedCrew(new Name(crewName), memberId);
    }

    @Transactional
    public void updateCrew(String targetCrewName, CrewUpdateDto dto, Long memberId, byte[] image, String imageName) {
        crewRepository.findCrewByNameForUpdate(new Name(targetCrewName))
                .ifPresentOrElse(
                        crew -> updateCrewInformation(dto, memberId, image, imageName, crew),
                        () -> { throw new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, dto.name()); }
                );
    }

    private void updateCrewInformation(CrewUpdateDto dto, Long memberId, byte[] imageData, String imageName, Crew crew) {
        validateCrewPublisher(memberId, crew, dto.name());

        uploadImageAndUpdateCrew(dto, imageData, imageName, crew);
        crewRepository.saveAndFlush(crew);
    }

    private void validateCrewPublisher(Long memberId, Crew crew, String crewName) {
        if (!crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, crewName);
        }
    }

    private void uploadImageAndUpdateCrew(CrewUpdateDto dto, byte[] imageData, String imageName, Crew crew) {
        String remoteAddress = crew.getImage().getImageUrl();
        s3BucketUploader.updateImage(remoteAddress, imageData, imageName);
        crew.updateCrew(dto.name(), dto.introduce(), dto.detail(), dto.hashTags(), dto.kakaoLink(), remoteAddress);
    }

    @Transactional
    public void acceptCrewMember(CrewAcceptDto dto, Long memberId) {
        crewRepository.findByName(new Name(dto.requestCrewName()))
                .ifPresentOrElse(
                        crew -> modifyCrewMemberMetadata(dto, memberId, crew),
                        () -> { throw new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, dto.requestCrewName()); }
                );
    }

    private void modifyCrewMemberMetadata(CrewAcceptDto dto, Long memberId, Crew crew) {
        validateCrewPublisher(memberId, crew, dto.requestCrewName());
        modifyCrewMemberJoinStatus(dto, memberId, crew.getId());
    }

    private void modifyCrewMemberJoinStatus(CrewAcceptDto dto, Long memberId, Long crewId) {
        crewMemberRepository.findCrewMemberByCrewIdAndMemberId(crewId, memberId)
                .ifPresentOrElse(
                        crewMember -> modifyJoinStatus(dto.requestStatus(), crewMember),
                        () -> { throw new CrewMemberBusinessException.NeverRequested(NEVER_REQUESTED, dto.requestCrewName()); }
                );
    }

    private void modifyJoinStatus(JoinStatus requestJoinStatus, CrewMember crewMember) {
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
