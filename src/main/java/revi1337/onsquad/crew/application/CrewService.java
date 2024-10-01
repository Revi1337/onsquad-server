package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.CrewInfoDto;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewJoinDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.hashtag.util.HashtagTypeUtil;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;
import revi1337.onsquad.member.domain.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewParticipantRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3BucketUploader s3BucketUploader;

    public boolean checkDuplicateNickname(String crewName) {
        return crewRepository.existsByName(new Name(crewName));
    }

    public CrewInfoDto findCrewById(Long crewId) {
        return CrewInfoDto.from(crewRepository.getCrewById(crewId));
    }

    public List<CrewInfoDto> findCrewsByName(String crewName, Pageable pageable) {
        return crewRepository.findCrewsByName(crewName, pageable).stream()
                .map(CrewInfoDto::from)
                .toList();
    }

    // TODO AWS 가 껴있기 때문에 트랜잭션 분리 필요.
    @Transactional
    public void createNewCrew(Long memberId, CrewCreateDto dto, byte[] image, String imageFileName) {
        List<HashtagType> hashtagTypes = HashtagTypeUtil.extractPossible(HashtagType.fromTexts(dto.hashTags()));
        Member member = memberRepository.getById(memberId);
        crewRepository.findByName(new Name(dto.name()))
                .ifPresent(ignored -> { throw new CrewBusinessException.AlreadyExists(ALREADY_EXISTS, dto.name()); });

        persistCrewAndRegisterOwner(dto, image, imageFileName, hashtagTypes, member);
    }

    @Transactional
    public void joinCrew(Long memberId, CrewJoinDto dto) {
        Crew crew = crewRepository.getById(dto.crewId());
        checkDifferenceCrewCreator(crew, memberId);
        crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), memberId).ifPresentOrElse(
                crewMember -> { throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, dto.crewId()); },
                () -> crewParticipantRepository.upsertCrewParticipant(crew.getId(), memberId, LocalDateTime.now())
        );
    }

    private void persistCrewAndRegisterOwner(CrewCreateDto dto, byte[] imageBinary, String imageFileName, List<HashtagType> hashtagTypes, Member member) {
        String uploadRemoteAddress = s3BucketUploader.uploadCrew(imageBinary, imageFileName);
        Crew crew = dto.toEntity(new Image(uploadRemoteAddress), member);
        crew.addCrewMember(CrewMember.forOwner(member, LocalDateTime.now()));
        crewRepository.save(crew);

        List<Hashtag> hashtags = Hashtag.fromHashtagTypes(hashtagTypes);
        crewRepository.batchInsertCrewHashtags(crew.getId(), hashtags);
    }

    private void checkDifferenceCrewCreator(Crew crew, Long memberId) {
        if (crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
        }
    }
}
