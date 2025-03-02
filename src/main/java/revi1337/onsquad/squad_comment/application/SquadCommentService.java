package revi1337.onsquad.squad_comment.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.NOTMATCH_CREWINFO;
import static revi1337.onsquad.squad_comment.error.SquadCommentErrorCode.NON_MATCH_SQUAD_ID;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_comment.application.dto.SquadCommentDto;
import revi1337.onsquad.squad_comment.domain.SquadComment;
import revi1337.onsquad.squad_comment.domain.SquadCommentRepository;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommentService {

    private final SquadCommentRepository squadCommentRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadRepository squadRepository;

    public Long addComment(Long memberId, Long crewId, Long squadId, String content) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Squad squad = squadRepository.getById(squadId);
        validateSquadInCrew(crewId, squad);

        SquadComment comment = SquadComment.create(content, squad, crewMember);
        SquadComment persistComment = squadCommentRepository.save(comment);

        return persistComment.getId();
    }

    public Long addCommentReply(Long memberId, Long crewId, Long squadId, Long parentId, String content) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Squad squad = squadRepository.getById(squadId);
        validateSquadInCrew(crewId, squad);

        SquadComment parentComment = squadCommentRepository.getById(parentId);
        validateHasSameSquadId(squadId, parentComment);
        SquadComment comment = SquadComment.createReply(parentComment, content, squad, crewMember);
        SquadComment persistComment = squadCommentRepository.save(comment);

        return persistComment.getId();
    }

    public List<SquadCommentDto> fetchParentCommentsWithChildren(Long memberId, Long crewId, Long squadId,
                                                                 Pageable pageable, int childSize) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return squadCommentRepository.fetchPageableParentCommentsWithLimitChildren(squadId, pageable, childSize)
                .stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    public List<SquadCommentDto> findMoreChildComments(Long memberId, Long crewId, Long squadId,
                                                       Long parentId, Pageable pageable) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return squadCommentRepository.findChildComments(squadId, parentId, pageable).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    public List<SquadCommentDto> findAllComments(Long memberId, Long crewId, Long squadId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return squadCommentRepository.findAllWithMemberBySquadId(squadId).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    private void validateSquadInCrew(Long crewId, Squad squad) {
        if (squad.hasNotSameCrewId(crewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }
    }

    private void validateHasSameSquadId(Long squadId, SquadComment parentComment) {
        if (parentComment.hasNotSameSquadId(squadId)) {
            throw new SquadCommentBusinessException.NonMatchSquadId(NON_MATCH_SQUAD_ID);
        }
    }
}
