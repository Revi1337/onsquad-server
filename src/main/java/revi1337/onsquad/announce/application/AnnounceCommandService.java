package revi1337.onsquad.announce.application;

import static revi1337.onsquad.announce.error.AnnounceErrorCode.CANT_FIX;
import static revi1337.onsquad.announce.error.AnnounceErrorCode.CANT_MAKE;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.Announce;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class AnnounceCommandService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Long newAnnounce(Long memberId, Long crewId, AnnounceCreateDto dto) {
        Crew crew = crewRepository.getById(crewId);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crew.getId(), memberId);
        checkMemberCanCreateAnnounce(crewMember);
        Announce persisteAnnounce = announceRepository.save(dto.toEntity(crew, crewMember));
        applicationEventPublisher.publishEvent(new AnnounceCreateEvent(crew.getId()));

        return persisteAnnounce.getId();
    }

    public void fixAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsCrewOwner(crewMember);
        Announce announce = announceRepository.getByIdAndCrewId(announceId, crewId);
        if (announce.isNotFixed()) {
            announce.fix(LocalDateTime.now());
            announceRepository.saveAndFlush(announce);
            applicationEventPublisher.publishEvent(new AnnounceFixedEvent(crewId, announceId));
        }
    }

    public void unfixAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsCrewOwner(crewMember);
        Announce announce = announceRepository.getByIdAndCrewId(announceId, crewId);
        if (announce.isFixed()) {
            announce.unfix();
            announceRepository.saveAndFlush(announce);
            applicationEventPublisher.publishEvent(new AnnounceFixedEvent(crewId, announceId));
        }
    }

    private void checkMemberCanCreateAnnounce(CrewMember crewMember) {
        if (crewMember.isNotGreaterThenManager()) {
            throw new AnnounceBusinessException.CantMake(CANT_MAKE);
        }
    }

    private void checkMemberIsCrewOwner(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new AnnounceBusinessException.CantFix(CANT_FIX);
        }
    }
}
