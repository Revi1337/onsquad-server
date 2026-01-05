package revi1337.onsquad.squad.application;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class SquadContextHandler {

    private final SquadRepository squadRepository;
    private final SquadRequestRepository squadRequestRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final SquadCommentRepository squadCommentRepository;
    private final SquadContextDisposer squadContextDisposer;

    public List<Long> findOwnedSquadIds(Long memberId, List<Long> ownedCrewIds) {
        List<Long> myOwnedSquadIds = squadRepository.findIdsByMemberId(memberId);
        List<Long> squadIdsInCrews = squadRepository.findIdsByCrewIdIn(ownedCrewIds);

        return Stream.concat(myOwnedSquadIds.stream(), squadIdsInCrews.stream())
                .distinct()
                .toList();
    }

    public void removeMemberFromSquads(Long memberId, List<Long> squadIdsToRemove) {
        squadContextDisposer.disposeContexts(squadIdsToRemove);
        cleanUpMemberData(memberId);
    }

    private void cleanUpMemberData(Long memberId) {
        squadRepository.decrementCountByMemberId(memberId);
        squadRequestRepository.deleteByMemberId(memberId);
        squadMemberRepository.deleteByMemberId(memberId);
        squadCommentRepository.deleteByMemberId(memberId);
    }
}
