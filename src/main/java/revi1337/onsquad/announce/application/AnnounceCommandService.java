package revi1337.onsquad.announce.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceUpdateDto;
import revi1337.onsquad.announce.domain.AnnouncePolicy;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.event.AnnounceDeleteEvent;
import revi1337.onsquad.announce.domain.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.event.AnnounceUpdateEvent;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnounceCommandService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final AnnounceAccessor announceAccessor;
    private final AnnounceRepository announceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void newAnnounce(Long memberId, Long crewId, AnnounceCreateDto dto) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        AnnouncePolicy.ensureWritable(me);
        announceRepository.save(dto.toEntity(me.getCrew(), me.getMember()));
        applicationEventPublisher.publishEvent(new AnnounceCreateEvent(crewId));
    }

    public void updateAnnounce(Long memberId, Long crewId, Long announceId, AnnounceUpdateDto dto) {
        Announce announce = announceAccessor.getById(announceId);
        AnnouncePolicy.ensureMatchCrew(announce, crewId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        AnnouncePolicy.ensureModifiable(announce, me);
        announce.update(dto.title(), dto.content());
        applicationEventPublisher.publishEvent(new AnnounceUpdateEvent(crewId, announce.getId()));
    }

    public void deleteAnnounce(Long memberId, Long crewId, Long announceId) {
        Announce announce = announceAccessor.getById(announceId);
        AnnouncePolicy.ensureMatchCrew(announce, crewId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        AnnouncePolicy.ensureDeletable(announce, me);
        announceRepository.delete(announce);
        applicationEventPublisher.publishEvent(new AnnounceDeleteEvent(crewId, announce.getId()));
    }

    public void changeFixState(Long memberId, Long crewId, Long announceId, boolean state) {
        Announce announce = announceAccessor.getById(announceId);
        AnnouncePolicy.ensureMatchCrew(announce, crewId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        AnnouncePolicy.ensureFixable(me);
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
}
