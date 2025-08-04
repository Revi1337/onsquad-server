package revi1337.onsquad.squad_comment.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.MISMATCH_REFERENCE;
import static revi1337.onsquad.squad_comment.error.SquadCommentErrorCode.MISMATCH_WRITER;
import static revi1337.onsquad.squad_comment.error.SquadCommentErrorCode.NOTFOUND_COMMENT;
import static revi1337.onsquad.squad_comment.error.SquadCommentErrorCode.NOT_PARENT;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_comment.domain.SquadComment;
import revi1337.onsquad.squad_comment.domain.SquadCommentRepository;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommentCommandService {

    private final CrewMemberRepository crewMemberRepository;
    private final SquadRepository squadRepository;
    private final SquadCommentRepository squadCommentRepository;

    public Long add(Long memberId, Long crewId, Long squadId, String content) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Squad squad = squadRepository.getById(squadId);
        checkSquadInCrew(squad, crewId);

        return persist(content, squad, crewMember);
    }

    public Long addReply(Long memberId, Long crewId, Long squadId, Long parentId, String content) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Optional<SquadComment> comment = squadCommentRepository.findByIdAndSquadIdAndCrewId(parentId, squadId, crewId);
        if (comment.isEmpty()) {
            throw new SquadCommentBusinessException.NotFound(NOTFOUND_COMMENT);
        }

        SquadComment squadComment = comment.get();
        checkCommentIsParent(squadComment);

        return persistReply(content, squadComment, crewMember);
    }

    public void update(Long memberId, Long crewId, Long squadId, Long commentId, String content) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadComment comment = squadCommentRepository.getWithSquadByIdAndSquadId(commentId, squadId);
        checkSquadInCrew(comment.getSquad(), crewId);
        checkCommentWriter(comment, crewMember);

        comment.update(content);
    }

    public void delete(Long memberId, Long crewId, Long squadId, Long commentId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadComment comment = squadCommentRepository.getWithSquadByIdAndSquadId(commentId, squadId);
        Squad squad = comment.getSquad();
        checkSquadInCrew(squad, crewId);

        if (squad.matchOwner(crewMember.getId()) || comment.matchWriterId(crewMember.getId())) {
            comment.delete();
            return;
        }

        throw new SquadCommentBusinessException.NonMatchWriterId(MISMATCH_WRITER);
    }

    private Long persist(String content, Squad squad, CrewMember crewMember) {
        SquadComment comment = SquadComment.create(content, squad, crewMember);
        SquadComment persistComment = squadCommentRepository.save(comment);

        return persistComment.getId();
    }

    private Long persistReply(String content, SquadComment parentComment, CrewMember crewMember) {
        Squad squad = parentComment.getSquad();
        SquadComment comment = SquadComment.createReply(parentComment, content, squad, crewMember);
        SquadComment persistComment = squadCommentRepository.save(comment);

        return persistComment.getId();
    }

    private void checkSquadInCrew(Squad squad, Long crewId) {
        if (squad.misMatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(MISMATCH_REFERENCE);
        }
    }

    private void checkCommentIsParent(SquadComment comment) {
        if (comment.isNotParent()) {
            throw new SquadCommentBusinessException.NotParent(NOT_PARENT);
        }
    }

    private void checkCommentWriter(SquadComment comment, CrewMember crewMember) {
        if (comment.misMatchWriterId(crewMember.getId())) {
            throw new SquadCommentBusinessException.NonMatchWriterId(MISMATCH_WRITER);
        }
    }
}
