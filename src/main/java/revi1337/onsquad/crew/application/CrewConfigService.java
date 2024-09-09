package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.application.dto.CrewAcceptDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantJpaRepository;

import java.time.LocalDateTime;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final CrewRepository crewRepository;
    private final CrewParticipantJpaRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3BucketUploader s3BucketUploader;

    @Transactional
    public void updateCrew(Long memberId, String targetCrewName, CrewUpdateDto dto, byte[] image, String imageName) {
        Crew crew = crewRepository.getCrewByNameWithImage(new Name(targetCrewName));
        validateCrewPublisher(memberId, crew, dto.name());
        uploadImageAndUpdateCrew(dto, image, imageName, crew);
    }

    @Transactional
    public void acceptCrewMember(Long memberId, CrewAcceptDto dto) {
        Crew crew = crewRepository.getByName(new Name(dto.crewName()));
        validateCrewPublisher(memberId, crew, dto.crewName());
        crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), dto.memberId()).ifPresentOrElse(
                crewMember -> { throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, dto.crewName()); },
                () -> {
                    CrewParticipant crewParticipant = crewParticipantRepository.getByCrewIdAndMemberId(crew.getId(), dto.memberId());
                    crewMemberRepository.save(CrewMember.forGeneral(crew, crewParticipant.getMember(), LocalDateTime.now()));
                    crewParticipantRepository.deleteById(crewParticipant.getId());
                }
        );
    }

    private void uploadImageAndUpdateCrew(CrewUpdateDto dto, byte[] imageData, String imageName, Crew crew) {
        String remoteAddress = crew.getImage().getImageUrl();
        s3BucketUploader.updateImage(remoteAddress, imageData, imageName);
        crew.updateCrew(dto.name(), dto.introduce(), dto.detail(), dto.hashTags(), dto.kakaoLink(), remoteAddress);
        crewRepository.saveAndFlush(crew);
    }

    private void validateCrewPublisher(Long memberId, Crew crew, String crewName) {
        if (!crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, crewName);
        }
    }
}
