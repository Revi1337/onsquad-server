package revi1337.onsquad.crew_participant.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_participant.domain.dto.CrewParticipantRequest;
import revi1337.onsquad.crew_participant.domain.dto.SimpleCrewParticipantRequest;
import revi1337.onsquad.member.domain.Member;

@RequiredArgsConstructor
@Repository
public class CrewParticipantRepositoryImpl implements CrewParticipantRepository {

    private final CrewParticipantJpaRepository crewParticipantJpaRepository;
    private final CrewParticipantJdbcRepository crewParticipantJdbcRepository;
    private final CrewParticipantQueryDslRepository crewParticipantQueryDslRepository;

    @Override
    public CrewParticipant save(CrewParticipant crewParticipant) {
        return crewParticipantJpaRepository.save(crewParticipant);
    }

    @Override
    public List<CrewParticipant> saveAll(List<CrewParticipant> crewParticipants) {
        return crewParticipantJpaRepository.saveAll(crewParticipants);
    }

    @Override
    public CrewParticipant saveAndFlush(CrewParticipant crewParticipant) {
        return crewParticipantJpaRepository.saveAndFlush(crewParticipant);
    }

    @Override
    public void deleteById(Long id) {
        crewParticipantJpaRepository.deleteById(id);
    }

    @Override
    public Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId) {
        return crewParticipantJpaRepository.findByCrewIdAndMemberId(crewId, memberId);
    }

    @Transactional
    @Override
    public CrewParticipant upsertCrewParticipant(Crew crew, Member member, LocalDateTime now) {
        return crewParticipantJpaRepository.findByCrewIdAndMemberId(crew.getId(), member.getId())
                .map(crewParticipant -> {
                    crewParticipant.updateRequestAt(now);
                    return crewParticipantJpaRepository.saveAndFlush(crewParticipant);
                })
                .orElseGet(() -> crewParticipantJpaRepository.save(new CrewParticipant(crew, member, now)));
    }

    @Override
    public List<CrewParticipantRequest> findMyCrewRequests(Long memberId) {
        return crewParticipantQueryDslRepository.findMyCrewRequests(memberId);
    }

    @Override
    public Page<SimpleCrewParticipantRequest> fetchCrewRequests(Long crewId, Pageable pageable) {
        return crewParticipantQueryDslRepository.fetchCrewRequests(crewId, pageable);
    }
}
