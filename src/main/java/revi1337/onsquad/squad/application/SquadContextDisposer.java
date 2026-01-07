package revi1337.onsquad.squad.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryRepository;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class SquadContextDisposer {

    private final SquadRepository squadRepository;
    private final SquadCategoryRepository squadCategoryRepository;
    private final SquadRequestRepository squadRequestRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final SquadCommentRepository squadCommentRepository;

    public void disposeContext(Long squadId) {
        deleteSquadRelatedData(List.of(squadId));
    }

    public void disposeContexts(List<Long> squadIds) {
        if (!squadIds.isEmpty()) {
            deleteSquadRelatedData(squadIds);
        }
    }

    public void disposeMemberActivity(Long memberId, List<Long> squadIdsToRemove) {
        disposeContexts(squadIdsToRemove);
        cleanUpMemberData(memberId);
    }

    private void deleteSquadRelatedData(List<Long> squadIds) {
        squadCategoryRepository.deleteBySquadIdIn(squadIds);
        squadRequestRepository.deleteBySquadIdIn(squadIds);
        squadMemberRepository.deleteBySquadIdIn(squadIds);
        squadCommentRepository.deleteBySquadIdIn(squadIds);
        squadRepository.deleteByIdIn(squadIds);
    }

    private void cleanUpMemberData(Long memberId) {
        squadRepository.decrementCountByMemberId(memberId);
        squadRequestRepository.deleteByMemberId(memberId);
        squadMemberRepository.deleteByMemberId(memberId);
        squadCommentRepository.deleteByMemberId(memberId);
    }
}
