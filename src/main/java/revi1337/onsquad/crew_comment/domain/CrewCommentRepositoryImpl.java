package revi1337.onsquad.crew_comment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_comment.domain.dto.CrewCommentDomainDto;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class CrewCommentRepositoryImpl implements CrewCommentRepository {

    private final CrewCommentJpaRepository crewCommentJpaRepository;
    private final CrewCommentQueryDslRepository crewCommentQueryDslRepository;
    private final CrewCommentJdbcRepository crewCommentJdbcRepository;

    @Override
    public CrewComment save(CrewComment crewComment) {
        return crewCommentJpaRepository.save(crewComment);
    }

    @Override
    public Optional<CrewComment> findById(Long id) {
        return crewCommentJpaRepository.findById(id);
    }

    @Override
    public List<CrewCommentDomainDto> findAllWithMemberByCrewId(Long crewId) {
        List<CrewCommentDomainDto> comments = crewCommentQueryDslRepository.findCommentsWithMemberByCrewId(crewId);
        return modifyCommentsHierarchy(comments);
    }

    private List<CrewCommentDomainDto> modifyCommentsHierarchy(List<CrewCommentDomainDto> comments) {
        List<CrewCommentDomainDto> commentList = new ArrayList<>();
        Map<Long, CrewCommentDomainDto> hashMap = new HashMap<>();
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
    public List<CrewCommentDomainDto> findLimitedCommentsBothOfParentsAndChildren(Long crewId, Pageable pageable, Integer childSize) {
        Map<Long, CrewCommentDomainDto> parentComments = crewCommentQueryDslRepository.findLimitedParentCommentsByCrewId(crewId, pageable);
        List<CrewCommentDomainDto> childComments = crewCommentJdbcRepository.findLimitedChildCommentsByParentIdIn(parentComments.keySet(), childSize);
        linkChildCommentsToParent(parentComments, childComments);

        return parentComments.values().stream()
                .map(comment -> parentComments.get(comment.commentId()))
                .toList();
    }

    private void linkChildCommentsToParent(Map<Long, CrewCommentDomainDto> parentCommentMap, List<CrewCommentDomainDto> childComments) {
        childComments.forEach(childComment -> {
            CrewCommentDomainDto crewCommentDomainDto = parentCommentMap.get(childComment.parentCommentId());
            crewCommentDomainDto.replies().add(childComment);
        });
    }

    @Override
    public List<CrewCommentDomainDto> findChildComments(Long crewId, Long parentId, Pageable pageable) {
        return crewCommentQueryDslRepository.findChildComments(crewId, parentId, pageable);
    }
}
