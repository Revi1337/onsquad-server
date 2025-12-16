package revi1337.onsquad.squad_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.SquadAccessPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.event.CommentAdded;
import revi1337.onsquad.squad_comment.domain.event.CommentReplyAdded;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommentCommandService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final SquadAccessPolicy squadAccessPolicy;
    private final SquadCommentAccessPolicy squadCommentAccessPolicy;
    private final SquadCommentRepository squadCommentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void add(Long memberId, Long crewId, Long squadId, String content) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        Squad squad = squadAccessPolicy.ensureSquadExistsAndGet(squadId);
        squadAccessPolicy.ensureMatchCrew(squad, crewId);
        SquadComment comment = squadCommentRepository.save(SquadComment.create(content, squad, crewMember.getMember()));
        eventPublisher.publishEvent(new CommentAdded(memberId, comment.getId()));
    }

    public void addReply(Long memberId, Long crewId, Long squadId, Long parentId, String content) {
        SquadComment parent = squadCommentAccessPolicy.ensureCommentExistsAndGet(parentId);
        squadCommentAccessPolicy.ensureMatchSquad(parent, squadId);
        squadCommentAccessPolicy.ensureCommentIsAlive(parent);
        squadCommentAccessPolicy.ensureCommentIsParent(parent);
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        squadAccessPolicy.ensureMatchCrew(parent.getSquad(), crewId);
        SquadComment reply = squadCommentRepository.save(SquadComment.createReply(parent, content, parent.getSquad(), crewMember.getMember()));
        eventPublisher.publishEvent(new CommentReplyAdded(parentId, memberId, reply.getId()));
    }

    public void update(Long memberId, Long crewId, Long squadId, Long commentId, String content) {
        SquadComment comment = squadCommentAccessPolicy.ensureCommentExistsAndGet(commentId);
        squadCommentAccessPolicy.ensureMatchSquad(comment, squadId);
        squadCommentAccessPolicy.ensureCommentIsAlive(comment);
        squadCommentAccessPolicy.ensureMatchWriter(comment, memberId);
        squadAccessPolicy.ensureMatchCrew(comment.getSquad(), crewId);
        comment.update(content);
    }

    public void delete(Long memberId, Long crewId, Long squadId, Long commentId) {
        SquadComment comment = squadCommentAccessPolicy.ensureCommentExistsAndGet(commentId);
        squadCommentAccessPolicy.ensureMatchSquad(comment, squadId);
        squadCommentAccessPolicy.ensureMatchWriter(comment, memberId);
        squadAccessPolicy.ensureMatchCrew(comment.getSquad(), crewId);
        comment.delete();
    }
}
