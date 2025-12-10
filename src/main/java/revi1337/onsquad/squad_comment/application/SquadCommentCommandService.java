package revi1337.onsquad.squad_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_comment.error.SquadCommentErrorCode;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommentCommandService { // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에

    private final CrewMemberRepository crewMemberRepository;
    private final SquadRepository squadRepository;
    private final SquadCommentRepository squadCommentRepository;

    public void add(Long memberId, Long crewId, Long squadId, String content) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        Squad squad = validateSquadExistsAndGet(squadId);
        if (squad.mismatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_CREW_REFERENCE);
        }
        squadCommentRepository.save(SquadComment.create(content, squad, crewMember.getMember()));
    }

    public void addReply(Long memberId, Long crewId, Long squadId, Long parentId, String content) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        Squad squad = validateSquadExistsAndGet(squadId);
        if (squad.mismatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_CREW_REFERENCE);
        }
        SquadComment comment = validateCommentExistsAndGet(parentId);
        if (comment.mismatchSquadId(squadId)) {
            throw new SquadCommentBusinessException.MismatchReference(SquadCommentErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
        if (comment.isNotParent()) {
            throw new SquadCommentBusinessException.NotParent(SquadCommentErrorCode.NOT_PARENT);
        }
        if (comment.isDeleted()) {
            throw new SquadCommentBusinessException.Deleted(SquadCommentErrorCode.DELETED);
        }
        squadCommentRepository.save(SquadComment.createReply(comment, content, comment.getSquad(), crewMember.getMember()));
    }

    public void update(Long memberId, Long crewId, Long squadId, Long commentId, String content) {
        validateMemberInCrew(memberId, crewId);
        SquadComment comment = validateCommentExistsAndGet(commentId);
        if (comment.mismatchSquadId(squadId)) {
            throw new SquadCommentBusinessException.MismatchReference(SquadCommentErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
        if (comment.mismatchWriterId(memberId)) {
            throw new SquadCommentBusinessException.MismatchReference(SquadCommentErrorCode.MISMATCH_WRITER);
        }
        if (comment.isDeleted()) {
            throw new SquadCommentBusinessException.Deleted(SquadCommentErrorCode.DELETED);
        }
        comment.update(content);
    }

    public void delete(Long memberId, Long crewId, Long squadId, Long commentId) {
        validateMemberInCrew(memberId, crewId);
        SquadComment comment = validateCommentExistsAndGet(commentId);
        if (comment.mismatchSquadId(squadId)) {
            throw new SquadCommentBusinessException.MismatchReference(SquadCommentErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
        if (comment.mismatchWriterId(memberId)) {
            throw new SquadCommentBusinessException.MismatchReference(SquadCommentErrorCode.MISMATCH_WRITER);
        }
        comment.delete();
    }

    private Squad validateSquadExistsAndGet(Long squadId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return squadRepository.findById(squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
    }

    private SquadComment validateCommentExistsAndGet(Long commentId) {
        return squadCommentRepository.findById(commentId)
                .orElseThrow(() -> new SquadCommentBusinessException.NotFound(SquadCommentErrorCode.NOTFOUND_COMMENT));
    }

    private void validateMemberInCrew(Long memberId, Long crewId) {
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty()) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }
    }

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) {
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }
}
