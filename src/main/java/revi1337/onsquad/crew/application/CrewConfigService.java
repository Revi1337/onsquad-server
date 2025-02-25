package revi1337.onsquad.crew.application;

import static revi1337.onsquad.crew.error.CrewErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.crew.error.CrewErrorCode.INVALID_PUBLISHER;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.crew.application.dto.CrewAcceptDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.application.event.CrewUpdateEvent;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtag;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtagRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantJpaRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrewConfigService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewParticipantJpaRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    // TODO AWS 가 껴있으니 트랜잭션 분리가 필요.
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

    @Transactional
    public void acceptCrewMember(Long memberId, CrewAcceptDto dto) {
        Crew crew = crewRepository.getById(dto.crewId());
        validateCrewPublisher(memberId, crew);
        crewMemberRepository.findByCrewIdAndMemberId(dto.crewId(), dto.memberId()).ifPresentOrElse(
                crewMember -> {
                    throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, dto.crewId());
                },
                () -> {
                    CrewParticipant crewParticipant = crewParticipantRepository.getByCrewIdAndMemberId(crew.getId(),
                            dto.memberId());
                    crewMemberRepository.save(
                            CrewMember.forGeneral(crew, crewParticipant.getMember(), LocalDateTime.now()));
                    crewParticipantRepository.deleteById(crewParticipant.getId());
                }
        );
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
        if (!crew.getMember().getId().equals(memberId)) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, crew.getId());
        }
    }
}
