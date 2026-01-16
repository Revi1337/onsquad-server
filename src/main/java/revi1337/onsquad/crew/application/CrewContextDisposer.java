package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CrewContextDisposer {

    private final CrewRepository crewRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final CrewRequestRepository crewRequestRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;

    public void disposeContext(Long crewId) {
        disposeContexts(List.of(crewId));
    }

    public void disposeContexts(List<Long> crewIds) {
        if (!crewIds.isEmpty()) {
            deleteCrewRelatedData(crewIds);
        }
    }

    public void disposeMemberActivity(Long memberId, List<Long> ownedCrewIds) {
        disposeContexts(ownedCrewIds);
        cleanUpMemberData(memberId);
    }

    private void deleteCrewRelatedData(List<Long> crewIds) {
        crewHashtagRepository.deleteByCrewIdIn(crewIds);
        crewRequestRepository.deleteByCrewIdIn(crewIds);
        crewMemberRepository.deleteByCrewIdIn(crewIds);
        announceRepository.deleteByCrewIdIn(crewIds);
        crewRepository.deleteByIdIn(crewIds);
    }

    private void cleanUpMemberData(Long memberId) {
        crewRepository.decrementCountByMemberId(memberId);
        crewRequestRepository.deleteByMemberId(memberId);
        crewMemberRepository.deleteByMemberId(memberId);
        announceRepository.markMemberAsNull(memberId);
    }
}
