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
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class AnnounceCommandService {

    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Long newAnnounce(Long memberId, Long crewId, AnnounceCreateDto dto) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isLessThenManager()) {
            throw new AnnounceBusinessException.CantMake(CANT_MAKE);
        }

        Announce announce = announceRepository.save(dto.toEntity(crewMember.getCrew(), crewMember));
        applicationEventPublisher.publishEvent(new AnnounceCreateEvent(crewId));

        return announce.getId();
    }

    public void fixOrUnfixAnnounce(Long memberId, Long crewId, Long announceId, boolean state) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new AnnounceBusinessException.CantFix(CANT_FIX);
        }

        Announce announce = announceRepository.getByIdAndCrewId(announceId, crewId);
        if (state) {
            fixAnnounce(crewId, announce);
        } else {
            unfixAnnounce(crewId, announce);
        }
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
}
