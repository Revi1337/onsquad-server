package revi1337.onsquad.announce.application;

import static revi1337.onsquad.announce.error.AnnounceErrorCode.INVALID_REFERENCE;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.LESS_THEN_MANAGER;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceUpdateDto;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.application.event.AnnounceDeleteEvent;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.application.event.AnnounceUpdateEvent;
import revi1337.onsquad.announce.domain.Announce;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class AnnounceCommandService {

    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Long newAnnounce(Long memberId, Long crewId, AnnounceCreateDto dto) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsGreaterThenGeneral(crewMember);

        Announce announce = announceRepository.save(dto.toEntity(crewMember.getCrew(), crewMember));
        applicationEventPublisher.publishEvent(new AnnounceCreateEvent(crewId));

        return announce.getId();
    }

    public void updateAnnounce(Long memberId, Long crewId, Long announceId, AnnounceUpdateDto dto) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsGreaterThenGeneral(crewMember);

        Announce announce = announceRepository.getByIdAndCrewId(announceId, crewId);
        if (cannotManagerUpdateOthersAnnounce(crewMember, announce)) {
            throw new AnnounceBusinessException.InvalidReference(INVALID_REFERENCE);
        }

        announce.update(dto.title(), dto.content());
        announceRepository.saveAndFlush(announce);
        applicationEventPublisher.publishEvent(new AnnounceUpdateEvent(crewId, announce.getId()));
    }

    public void deleteAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsGreaterThenGeneral(crewMember);

        Announce announce = announceRepository.getByIdAndCrewId(announceId, crewId);
        if (cannotManagerUpdateOthersAnnounce(crewMember, announce)) {
            throw new AnnounceBusinessException.InvalidReference(INVALID_REFERENCE);
        }

        announceRepository.delete(announce);
        applicationEventPublisher.publishEvent(new AnnounceDeleteEvent(crewId, announce.getId()));
    }

    public void fixOrUnfixAnnounce(Long memberId, Long crewId, Long announceId, boolean state) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        Announce announce = announceRepository.getByIdAndCrewId(announceId, crewId);
        if (state) {
            fixAnnounce(crewId, announce);
            return;
        }

        unfixAnnounce(crewId, announce);
    }

    private void fixAnnounce(Long crewId, Announce announce) {
        if (announce.isNotFixed()) {
            announce.fix(LocalDateTime.now());
            announceRepository.saveAndFlush(announce);
            applicationEventPublisher.publishEvent(new AnnounceFixedEvent(crewId, announce.getId()));
        }
    }

    private void unfixAnnounce(Long crewId, Announce announce) {
        if (announce.isFixed()) {
            announce.unfix();
            announceRepository.saveAndFlush(announce);
            applicationEventPublisher.publishEvent(new AnnounceFixedEvent(crewId, announce.getId()));
        }
    }

    private void checkMemberIsGreaterThenGeneral(CrewMember crewMember) {
        if (crewMember.isLessThenManager()) {
            throw new CrewMemberBusinessException.LessThenManager(LESS_THEN_MANAGER);
        }
    }

    private boolean cannotManagerUpdateOthersAnnounce(CrewMember crewMember, Announce announce) {
        return crewMember.isManager() && announce.isNotMatchWriterId(crewMember.getId());
    }
}
