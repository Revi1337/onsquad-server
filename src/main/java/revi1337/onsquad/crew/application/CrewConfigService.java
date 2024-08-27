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
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
import revi1337.onsquad.participant.domain.CrewParticipantRepository;

import java.util.List;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NEVER_REQUESTED;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final CrewRepository crewRepository;
    private final CrewParticipantRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3BucketUploader s3BucketUploader;

    public List<OwnedCrewsDto> findOwnedCrews(Long memberId) {
        return crewRepository.findOwnedCrews(memberId).stream()
                .toList();
    }

    public List<EnrolledCrewMemberDto> findMembersForSpecifiedCrew(String crewName, Long memberId) {
        return crewMemberRepository.findMembersForSpecifiedCrew(new Name(crewName), memberId);
    }

    @Transactional
    public void updateCrew(Long memberId, String targetCrewName, CrewUpdateDto dto, byte[] image, String imageName) {
        Crew crew = crewRepository.getCrewByNameWithImage(new Name(targetCrewName));
        updateCrewInfo(memberId, dto, image, imageName, crew);
    }

    @Transactional
    public void acceptCrewMember(Long memberId, CrewAcceptDto dto) {
        Crew crew = crewRepository.getByName(new Name(dto.requestCrewName()));
        acceptCrewJoinRequest(memberId, dto, crew);
    }

    private void updateCrewInfo(Long memberId, CrewUpdateDto dto, byte[] imageData, String imageName, Crew crew) {
        validateCrewPublisher(memberId, crew, dto.name());
        uploadImageAndUpdateCrew(dto, imageData, imageName, crew);
    }

    private void uploadImageAndUpdateCrew(CrewUpdateDto dto, byte[] imageData, String imageName, Crew crew) {
        String remoteAddress = crew.getImage().getImageUrl();
        s3BucketUploader.updateImage(remoteAddress, imageData, imageName);
        crew.updateCrew(dto.name(), dto.introduce(), dto.detail(), dto.hashTags(), dto.kakaoLink(), remoteAddress);
        crewRepository.saveAndFlush(crew);
    }

    private void acceptCrewJoinRequest(Long memberId, CrewAcceptDto dto, Crew crew) {
        validateCrewPublisher(memberId, crew, dto.requestCrewName());
        joinMemberInCrew(dto, crew);
    }

    private void validateCrewPublisher(Long memberId, Crew crew, String crewName) {
        if (!crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, crewName);
        }
    }

    private void joinMemberInCrew(CrewAcceptDto dto, Crew crew) {
        crewParticipantRepository.findByCrewIdAndMemberId(crew.getId(), dto.requestMemberId())
                .ifPresentOrElse(
                        crewParticipant -> {
                            crewParticipantRepository.delete(crewParticipant);
                            crewMemberRepository.save(CrewMember.forGeneral(crew, crewParticipant.getMember()));
                        },
                        () -> { throw new CrewMemberBusinessException.NeverRequested(NEVER_REQUESTED); }
                );
    }
}
