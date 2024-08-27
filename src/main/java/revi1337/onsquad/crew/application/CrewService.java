package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewCreateDto;
import revi1337.onsquad.crew.dto.CrewJoinDto;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.participant.domain.CrewParticipant;
import revi1337.onsquad.participant.domain.CrewParticipantRepository;

import java.time.LocalDateTime;
import java.util.List;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewParticipantRepository crewParticipantRepository;
    private final S3BucketUploader s3BucketUploader;

    public boolean checkDuplicateNickname(String crewName) {
        return crewRepository.existsByName(new Name(crewName));
    }

    public CrewWithMemberAndImageDto findCrewByName(String crewName) {
        return crewRepository.getCrewByName(new Name(crewName));
    }

    public List<CrewWithMemberAndImageDto> findCrewsByName() {
        return crewRepository.findCrewsByName();
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
        memberRepository.findById(memberId).ifPresent(
                member -> crewRepository.findByNameWithCrewMembers(new Name(crewJoinDto.crewName())).ifPresentOrElse(
                        crew -> addParticipantRequest(crew, member),
                        () -> { throw new CrewBusinessException.CannotJoin(CANNOT_JOIN, crewJoinDto.crewName()); }
                )
        );
    }

    private void persistCrewAndRegisterOwner(CrewCreateDto crewCreateDto, byte[] imageBinary, String imageFileName, Member member) {
        String uploadRemoteAddress = s3BucketUploader.uploadCrew(imageBinary, imageFileName);
        Crew crew = crewCreateDto.toEntity(new Image(uploadRemoteAddress), member);
        crew.addCrewMember(CrewMember.forOwner(member));
        crewRepository.save(crew);
    }

    private void addParticipantRequest(Crew crew, Member member) {
        checkDifferenceCrewCreator(crew, member);
        checkMemberInCrew(crew, member);
        persistCrewParticipant(crew, member);
    }

    private void checkMemberInCrew(Crew crew, Member member) {
        crew.getCrewMembers().stream()
                .filter(crewMember -> crewMember.getMember().equals(member))
                .findFirst()
                .ifPresent(ignored -> {
                    throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, crew.getName().getValue());
                });
    }

    private void checkDifferenceCrewCreator(Crew crew, Member member) {
        if (crew.getMember().equals(member)) {
            throw new CrewBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
        }
    }

    private synchronized void persistCrewParticipant(Crew crew, Member member) {
        crewParticipantRepository.findByCrewIdAndMemberIdUsingLock(crew.getId(), member.getId())
                .ifPresentOrElse(
                        crewParticipant -> {
                            crewParticipant.updateRequestTimestamp(LocalDateTime.now());
                            crewParticipantRepository.saveAndFlush(crewParticipant);
                        },
                        () -> crewParticipantRepository.save(CrewParticipant.of(crew, member, LocalDateTime.now()))
                );
    }
}
