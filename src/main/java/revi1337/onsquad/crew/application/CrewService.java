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
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

import java.time.LocalDateTime;
import java.util.List;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;
import static revi1337.onsquad.member.error.MemberErrorCode.*;

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

    public CrewInfoDto findCrewByName(String crewName) {
        return CrewInfoDto.from(crewRepository.getCrewByName(new Name(crewName)));
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
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(NOTFOUND, memberId));
        crewRepository.findByName(new Name(dto.name()))
                .ifPresent(ignored -> { throw new CrewBusinessException.AlreadyExists(ALREADY_EXISTS, dto.name()); });

        persistCrewAndRegisterOwner(dto, image, imageFileName, hashtagTypes, member);
    }

    @Transactional
    public void joinCrew(Long memberId, CrewJoinDto crewJoinDto) {
        Crew crew = crewRepository.getByName(new Name(crewJoinDto.crewName()));
        checkDifferenceCrewCreator(crew, memberId);
        crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), memberId).ifPresentOrElse(
                crewMember -> { throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, crew.getName().getValue()); },
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
