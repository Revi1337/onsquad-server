package revi1337.onsquad.squad_comment.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@RequiredArgsConstructor
@Repository
public class SquadCommentRepositoryImpl implements SquadCommentRepository {

    private final SquadCommentJpaRepository squadCommentJpaRepository;
    private final SquadCommentQueryDslRepository squadCommentQueryDslRepository;
    private final SquadCommentJdbcRepository squadCommentJdbcRepository;

    @Override
    public SquadComment save(SquadComment crewComment) {
        return squadCommentJpaRepository.save(crewComment);
    }

    @Override
    public Optional<SquadComment> findById(Long id) {
        return squadCommentJpaRepository.findById(id);
    }

    @Override
    public List<SquadCommentDomainDto> findAllWithMemberByCrewId(Long squadId) {
        List<SquadCommentDomainDto> comments = squadCommentQueryDslRepository.findCommentsWithMemberByCrewId(squadId);
        return modifyCommentsHierarchy(comments);
    }

    private List<SquadCommentDomainDto> modifyCommentsHierarchy(List<SquadCommentDomainDto> comments) {
        List<SquadCommentDomainDto> commentList = new ArrayList<>();
        Map<Long, SquadCommentDomainDto> hashMap = new HashMap<>();
        comments.forEach(comment -> {
            hashMap.put(comment.commentId(), comment);
            if (comment.parentCommentId() != null) {
                hashMap.get(comment.parentCommentId()).replies().add(comment);
            } else {
                commentList.add(comment);
            }
        });

        return commentList;
    }

    @Override
    public List<SquadCommentDomainDto> findLimitedCommentsBothOfParentsAndChildren(Long squadId, Pageable pageable,
                                                                                   Integer childSize) {
        Map<Long, SquadCommentDomainDto> parentComments = squadCommentQueryDslRepository.findLimitedParentCommentsByCrewId(
                squadId, pageable);
        List<SquadCommentDomainDto> childComments = squadCommentJdbcRepository.findLimitedChildCommentsByParentIdIn(
                parentComments.keySet(), childSize);
        linkChildCommentsToParent(parentComments, childComments);

        return parentComments.values().stream()
                .map(comment -> parentComments.get(comment.commentId()))
                .toList();
    }

    private void linkChildCommentsToParent(Map<Long, SquadCommentDomainDto> parentCommentMap,
                                           List<SquadCommentDomainDto> childComments) {
        childComments.forEach(childComment -> {
            SquadCommentDomainDto squadCommentDomainDto = parentCommentMap.get(childComment.parentCommentId());
            squadCommentDomainDto.replies().add(childComment);
        });
    }

    @Override
    public List<SquadCommentDomainDto> findChildComments(Long squadId, Long parentId, Pageable pageable) {
        return squadCommentQueryDslRepository.findChildComments(squadId, parentId, pageable);
    }
}
