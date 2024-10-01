package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.application.dto.CrewAcceptDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtag;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtagRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.hashtag.util.HashtagTypeUtil;
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final CrewRepository crewRepository;
    private final CrewParticipantJpaRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final S3BucketUploader s3BucketUploader;

    // TODO AWS 가 껴있으니 트랜잭션 분리가 필요.
    @Transactional
    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto, byte[] image, String imageName) {
        Crew crew = crewRepository.getByIdWithImage(crewId);
        validateCrewPublisher(memberId, crew);
        updateCrewInfo(dto, crew);
        s3BucketUploader.updateImage(crew.getImage().getImageUrl(), image, imageName);
    }

    private void updateCrewInfo(CrewUpdateDto dto, Crew crew) {
        crew.updateCrew(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewRepository.saveAndFlush(crew);
        updateCrewHashtags(dto, crew);
    }

    private void updateCrewHashtags(CrewUpdateDto dto, Crew crew) {
        List<HashtagType> hashtagTypes = HashtagTypeUtil.extractPossible(HashtagType.fromTexts(dto.hashTags()));
        List<Hashtag> hashtags = Hashtag.fromHashtagTypes(hashtagTypes);
        List<Long> hashtagIds = crew.getHashtags().stream().map(CrewHashtag::getId).toList();
        crewHashtagRepository.deleteAllByIdIn(hashtagIds);
        crewHashtagRepository.batchInsertCrewHashtags(crew.getId(), hashtags);
    }

    @Transactional
    public void acceptCrewMember(Long memberId, Long crewId, CrewAcceptDto dto) {
        Crew crew = crewRepository.getById(crewId);
        validateCrewPublisher(memberId, crew);
        crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), dto.memberId()).ifPresentOrElse(
                crewMember -> { throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, crewId); },
                () -> {
                    CrewParticipant crewParticipant = crewParticipantRepository.getByCrewIdAndMemberId(crew.getId(), dto.memberId());
                    crewMemberRepository.save(CrewMember.forGeneral(crew, crewParticipant.getMember(), LocalDateTime.now()));
                    crewParticipantRepository.deleteById(crewParticipant.getId());
                }
        );
    }

    private void validateCrewPublisher(Long memberId, Crew crew) {
        if (!crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, crew.getId());
        }
    }
}
