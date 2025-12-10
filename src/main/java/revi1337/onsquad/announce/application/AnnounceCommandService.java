package revi1337.onsquad.announce.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceUpdateDto;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.event.AnnounceDeleteEvent;
import revi1337.onsquad.announce.domain.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.event.AnnounceUpdateEvent;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.announce.error.AnnounceErrorCode;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

@RequiredArgsConstructor
@Transactional
@Service
public class AnnounceCommandService { // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에

    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void newAnnounce(Long memberId, Long crewId, AnnounceCreateDto dto) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        if (crewMember.isLessThenManager()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.LESS_THAN_MANAGER);
        }
        announceRepository.save(dto.toEntity(crewMember.getCrew(), crewMember));
        applicationEventPublisher.publishEvent(new AnnounceCreateEvent(crewId));
    }

    public void updateAnnounce(Long memberId, Long crewId, Long announceId, AnnounceUpdateDto dto) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        if (crewMember.isLessThenManager()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.LESS_THAN_MANAGER);
        }
        Announce announce = validateAnnounceExistsAndGet(announceId);
        if (announce.mismatchCrewId(crewId)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
        if (crewMember.isManager() && announce.mismatchMemberId(crewMember.getId())) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
        announce.update(dto.title(), dto.content());
        applicationEventPublisher.publishEvent(new AnnounceUpdateEvent(crewId, announce.getId()));
    }

    public void deleteAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        if (crewMember.isLessThenManager()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.LESS_THAN_MANAGER);
        }
        Announce announce = validateAnnounceExistsAndGet(announceId);
        if (announce.mismatchCrewId(crewId)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
        if (crewMember.isManager() && announce.mismatchMemberId(crewMember.getId())) {
            throw new AnnounceBusinessException.InsufficientAuthority(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
        announceRepository.delete(announce);
        applicationEventPublisher.publishEvent(new AnnounceDeleteEvent(crewId, announce.getId()));
    }

    public void fixOrUnfixAnnounce(Long memberId, Long crewId, Long announceId, boolean state) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
        Announce announce = validateAnnounceExistsAndGet(announceId);
        if (announce.mismatchCrewId(crewId)) {
            throw new AnnounceBusinessException.MismatchReference(AnnounceErrorCode.MISMATCH_CREW_REFERENCE);
        }
        if (state) {
            fixAnnounce(crewId, announce);
            return;
        }
        unfixAnnounce(crewId, announce);
    }

    private void fixAnnounce(Long crewId, Announce announce) {
        if (announce.isUnfixed()) {
            announce.fix(LocalDateTime.now());
            applicationEventPublisher.publishEvent(new AnnounceFixedEvent(crewId, announce.getId()));
        }
    }

    private void unfixAnnounce(Long crewId, Announce announce) {
        if (announce.isFixed()) {
            announce.unfix();
            applicationEventPublisher.publishEvent(new AnnounceFixedEvent(crewId, announce.getId()));
        }
    }

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) {  // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }

    private Announce validateAnnounceExistsAndGet(Long announceId) {
        return announceRepository.findById(announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }
}
