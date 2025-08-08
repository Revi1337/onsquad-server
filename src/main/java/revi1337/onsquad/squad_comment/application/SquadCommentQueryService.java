package revi1337.onsquad.squad_comment.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad_comment.application.dto.SquadCommentDto;
import revi1337.onsquad.squad_comment.domain.SquadCommentRepository;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadCommentQueryService {

    private final CrewMemberRepository crewMemberRepository;
    private final SquadCommentRepository commentRepository;
    private final CommentCombinator commentCombinator;
    private final CommentSanitizer commentSanitizer;

    public List<SquadCommentDto> fetchInitialComments(Long memberId, Long crewId, Long squadId, Pageable pageable, int childSize) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        List<SquadCommentDomainDto> parents = commentRepository.fetchAllParentsBySquadId(squadId, pageable);
        if (parents.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> parentIds = parents.stream().map(SquadCommentDomainDto::id).toList();
        List<SquadCommentDomainDto> replies = commentRepository.fetchAllChildrenByParentIdIn(parentIds, childSize);
        List<SquadCommentDomainDto> comments = commentCombinator.combine(parents, replies);

        return commentSanitizer.sanitizeUsingStack(comments).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    public List<SquadCommentDto> fetchMoreChildren(Long memberId, Long crewId, Long squadId, Long parentId, Pageable pageable) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return commentRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    @Deprecated
    public List<SquadCommentDto> findAllComments(Long memberId, Long crewId, Long squadId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        List<SquadCommentDomainDto> comments = commentRepository.findAllWithMemberBySquadId(squadId);

        return commentCombinator.makeHierarchy(comments).stream()
                .map(SquadCommentDto::from)
                .toList();
    }
}
