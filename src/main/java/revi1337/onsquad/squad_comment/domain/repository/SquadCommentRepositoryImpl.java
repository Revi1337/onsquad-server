package revi1337.onsquad.squad_comment.domain.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

@Repository
@RequiredArgsConstructor
public class SquadCommentRepositoryImpl implements SquadCommentRepository {

    private final SquadCommentJpaRepository squadCommentJpaRepository;
    private final SquadCommentQueryDslRepository squadCommentQueryDslRepository;

    @Override
    public SquadComment save(SquadComment comment) {
        return squadCommentJpaRepository.save(comment);
    }

    @Override
    public Optional<SquadComment> findById(Long id) {
        return squadCommentJpaRepository.findById(id);
    }

    @Override
    public Optional<SquadComment> findWithSquadById(Long id) {
        return squadCommentJpaRepository.findWithSquadById(id);
    }

    @Override
    public Page<SquadComment> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
        return squadCommentQueryDslRepository.fetchAllParentsBySquadId(squadId, pageable);
    }

    @Override
    public Page<SquadComment> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable) {
        return squadCommentQueryDslRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable);
    }

    @Override
    public int deleteByMemberId(Long memberId) {
        // TODO 부모 댓글에 타인의 답글이 있으면 함께 삭제하는 대신 마스킹 처리하는 방안 검토 (squad_comment.member_id nullable 전환과 조회 쿼리 leftJoin 필요)
        List<Long> replyIds = squadCommentJpaRepository.findReplyIdsByParentWriterId(memberId);
        int deletedReplies = replyIds.isEmpty() ? 0 : squadCommentJpaRepository.deleteByIdIn(replyIds);
        int deletedOwnComments = squadCommentJpaRepository.deleteByMemberId(memberId);
        return deletedReplies + deletedOwnComments;
    }

    @Override
    public int deleteBySquadIdIn(List<Long> squadIds) {
        int deletedReplies = squadCommentJpaRepository.deleteRepliesBySquadIdIn(squadIds);
        int deletedParents = squadCommentJpaRepository.deleteParentsBySquadIdIn(squadIds);
        return deletedReplies + deletedParents;
    }
}
