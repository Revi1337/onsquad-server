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
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;

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

    public CrewInfoDto findCrewByName(String crewName) {
        return CrewInfoDto.from(crewRepository.getCrewByName(new Name(crewName)));
    }

    public List<CrewInfoDto> findCrewsByName(String crewName, Pageable pageable) {
        return crewRepository.findCrewsByName(crewName, pageable).stream()
                .map(CrewInfoDto::from)
                .toList();
    }

    @Transactional
    public void createNewCrew(Long memberId, CrewCreateDto crewCreateDto, byte[] image, String imageFileName) {
        memberRepository.findById(memberId).ifPresent(
                member -> crewRepository.findByName(new Name(crewCreateDto.name())).ifPresentOrElse(
                        ignored -> { throw new CrewBusinessException.AlreadyExists(ALREADY_EXISTS, crewCreateDto.name()); },
                        () -> persistCrewAndRegisterOwner(crewCreateDto, image, imageFileName, member)
                )
        );
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

    private void persistCrewAndRegisterOwner(CrewCreateDto crewCreateDto, byte[] imageBinary, String imageFileName, Member member) {
        String uploadRemoteAddress = s3BucketUploader.uploadCrew(imageBinary, imageFileName);
        Crew crew = crewCreateDto.toEntity(new Image(uploadRemoteAddress), member);
        crew.addCrewMember(CrewMember.forOwner(member, LocalDateTime.now()));
        crewRepository.save(crew);
    }

    private void checkDifferenceCrewCreator(Crew crew, Long memberId) {
        if (crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
        }
    }
}
