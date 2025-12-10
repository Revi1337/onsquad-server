package revi1337.onsquad.squad_comment.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad_comment.application.dto.SquadCommentDto;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadCommentQueryService {

    private final CrewMemberRepository crewMemberRepository;
    private final SquadCommentRepository commentRepository;
    private final CommentCombinator commentCombinator;
    private final CommentSanitizer commentSanitizer;

    public List<SquadCommentDto> fetchInitialComments(Long memberId, Long crewId, Long squadId, Pageable pageable, int childSize) {
        validateMemberInCrew(memberId, crewId);
        List<SquadCommentDomainDto> parents = commentRepository.fetchAllParentsBySquadId(squadId, pageable);
        if (parents.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> parentIds = parents.stream().map(SquadCommentDomainDto::id).toList();
        List<SquadCommentDomainDto> replies = commentRepository.fetchAllChildrenByParentIdIn(parentIds, childSize);
        List<SquadCommentDomainDto> comments = commentCombinator.combine(parents, replies);

        return commentSanitizer.sanitize(comments).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    public List<SquadCommentDto> fetchMoreChildren(Long memberId, Long crewId, Long squadId, Long parentId, Pageable pageable) {
        validateMemberInCrew(memberId, crewId);
        return commentRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    private void validateMemberInCrew(Long memberId, Long crewId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty()) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }
    }
}
