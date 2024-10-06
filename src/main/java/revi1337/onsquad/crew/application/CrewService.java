package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.common.aspect.Throttling;
import revi1337.onsquad.crew.application.dto.CrewInfoDto;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewJoinDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.hashtag.util.HashtagTypeUtil;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.inrastructure.s3.application.DefaultS3FileManager;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;
import revi1337.onsquad.member.domain.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

import static revi1337.onsquad.common.aspect.OnSquadType.*;
import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrewService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewParticipantRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final DefaultS3FileManager crewS3FileManager;

    public boolean checkDuplicateNickname(String crewName) {
        return crewRepository.existsByName(new Name(crewName));
    }

    public void createNewCrew(Long memberId, CrewCreateDto dto, byte[] image, String imageFileName) {
        List<HashtagType> hashtagTypes = HashtagTypeUtil.extractPossible(HashtagType.fromTexts(dto.hashTags()));
        Member member = memberRepository.getById(memberId);
        crewRepository.findByName(new Name(dto.name())).ifPresentOrElse(
                ignored -> { throw new CrewBusinessException.AlreadyExists(ALREADY_EXISTS, dto.name()); },
                () -> {
                    Image crewImage = new Image(crewS3FileManager.uploadFile(image, imageFileName));
                    crewRepository.persistCrew(dto.toEntity(crewImage, member), Hashtag.fromHashtagTypes(hashtagTypes));
                }
        );
    }

    @Throttling(type = MEMBER, id = "memberId", perCycle = 3)
    public void joinCrew(Long memberId, CrewJoinDto dto) {
        Crew crew = crewRepository.getById(dto.crewId());
        crewMemberRepository.findByCrewIdAndMemberId(dto.crewId(), memberId).ifPresentOrElse(
                crewMember -> { throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, dto.crewId()); },
                () -> {
                    checkDifferenceCrewCreator(crew, memberId);
                    Member referenceMember = memberRepository.getReferenceById(memberId);
                    crewParticipantRepository.upsertCrewParticipant(crew, referenceMember, LocalDateTime.now());
                }
        );
    }

    public CrewInfoDto findCrewById(Long crewId) {
        return CrewInfoDto.from(crewRepository.getCrewById(crewId));
    }

    public List<CrewInfoDto> findCrewsByName(String crewName, Pageable pageable) {
        return crewRepository.findCrewsByName(crewName, pageable).stream()
                .map(CrewInfoDto::from)
                .toList();
    }

    private void checkDifferenceCrewCreator(Crew crew, Long memberId) {
        if (crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
        }
    }
}
