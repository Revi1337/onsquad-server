package revi1337.onsquad.crew.application;

import static revi1337.onsquad.crew.error.CrewErrorCode.ALREADY_EXISTS;
import static revi1337.onsquad.crew.error.CrewErrorCode.INVALID_PUBLISHER;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewInfoDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.application.event.CrewUpdateEvent;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtag;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtagRepository;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.inrastructure.s3.application.S3StorageManager;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final S3StorageManager crewS3StorageManager;

    public boolean isDuplicateCrewName(String crewName) {
        return crewRepository.existsByName(new Name(crewName));
    }

    /**
     * 1. S3 업로드 후, Crew 커밋.
     * <p>
     * - 업로드 후 @TransactionalEventListener 에서 Crew 만 커밋만 치면됨.
     * <p>
     * - 만약 Crew 에서 검증 오류가 발생한다면 따로 스케줄러로 삭제해주어야한다는 단점이 있음.
     * <p>
     * <p>
     * 2. Crew 커밋 후, S3 업로드.
     * <p>
     * - 커밋 후, S3 에 업로드하기 때문에 업로드 후, Crew 를 Update 시켜주어야한다는 단점이 있음.
     * <p>
     * - Crew 를 Update 하는 트랜잭션이 S3 업로드하는 로직과 함께하기 때문에, 데이터 불일치가 있을 수 있음.
     */
    // TODO 트랜잭션 분리 필요.
    @Transactional
    public void createNewCrew(Long memberId, CrewCreateDto dto, MultipartFile file) {
        Member member = memberRepository.getById(memberId);
        if (isDuplicateCrewName(dto.name())) {
            throw new CrewBusinessException.AlreadyExists(ALREADY_EXISTS, dto.name());
        }
        if (file != null) {
            Image crewImage = new Image(crewS3StorageManager.uploadFile(file));
            crewRepository.persistCrew(dto.toEntity(crewImage, member), dto.hashtags());
            return;
        }
        crewRepository.persistCrew(dto.toEntity(member), dto.hashtags());
    }

    public CrewInfoDto findCrewById(Long crewId) {
        return CrewInfoDto.from(crewRepository.getCrewById(crewId));
    }

    public CrewInfoDto findCrewById(Long memberId, Long crewId) {
        return CrewInfoDto.from(
                crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId),
                crewRepository.getCrewById(crewId)
        );
    }

    public List<CrewInfoDto> findCrewsByName(String crewName, Pageable pageable) {
        return crewRepository.findCrewsByName(crewName, pageable).stream()
                .map(CrewInfoDto::from)
                .toList();
    }

    @Transactional
    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto, MultipartFile file) {
        Member member = memberRepository.getById(memberId);
        Crew crew = crewRepository.getByIdWithImage(crewId);
        validateCrewPublisher(member.getId(), crew);
        update(dto, crew);
        publishEventIfMultipartAvailable(file, crewId);
    }

    private void update(CrewUpdateDto dto, Crew crew) {
        crew.updateCrew(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewRepository.saveAndFlush(crew);
        updateCrewHashtags(dto, crew);
    }

    private void updateCrewHashtags(CrewUpdateDto dto, Crew crew) {
        List<Hashtag> hashtags = dto.hashtags();
        List<Long> hashtagIds = crew.getHashtags().stream().map(CrewHashtag::getId).toList();
        crewHashtagRepository.deleteAllByIdIn(hashtagIds);
        crewHashtagRepository.batchInsertCrewHashtags(crew.getId(), hashtags);
    }

    private void publishEventIfMultipartAvailable(MultipartFile file, Long crewId) {
        if (file == null || file.isEmpty()) {
            return;
        }
        try {
            applicationEventPublisher.publishEvent(
                    new CrewUpdateEvent(crewId, file.getBytes(), file.getOriginalFilename())
            );
        } catch (IOException ignored) {
        }
    }

    private void validateCrewPublisher(Long memberId, Crew crew) {
        if (crew.isCreatedByOwnerUsing(memberId)) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, crew.getId());
        }
    }
}
