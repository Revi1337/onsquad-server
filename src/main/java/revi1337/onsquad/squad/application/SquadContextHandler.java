package revi1337.onsquad.squad.application;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.domain.repository.SquadRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class SquadContextHandler {

    private final SquadRepository squadRepository;
    private final SquadContextDisposer squadContextDisposer;

    public List<Long> findSquadIdsByCrewIdIn(List<Long> crewIds) {
        return squadRepository.findIdsByCrewIdIn(crewIds);
    }

    public List<Long> findOwnedSquadIds(Long memberId, List<Long> ownedCrewIds) {
        List<Long> myOwnedSquadIds = squadRepository.findIdsByMemberId(memberId);
        List<Long> squadIdsInCrews = squadRepository.findIdsByCrewIdIn(ownedCrewIds);

        return Stream.concat(myOwnedSquadIds.stream(), squadIdsInCrews.stream())
                .distinct()
                .toList();
    }

    public void disposeContext(Long squadId) {
        squadContextDisposer.disposeContext(squadId);
    }

    public void disposeContexts(List<Long> squadIds) {
        squadContextDisposer.disposeContexts(squadIds);
    }

    public void removeMemberFromSquads(Long memberId, List<Long> squadIdsToRemove) {
        squadContextDisposer.disposeMemberActivity(memberId, squadIdsToRemove);
    }
}
