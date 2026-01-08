package revi1337.onsquad.squad_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_comment.domain.SquadCommentPolicy;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.event.CommentAdded;
import revi1337.onsquad.squad_comment.domain.event.CommentReplyAdded;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_member.application.SquadMemberAccessor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@RequiredArgsConstructor
@Transactional
@Service
public class SquadCommentCommandService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadMemberAccessor squadMemberAccessor;
    private final SquadCommentAccessor squadCommentAccessor;
    private final SquadCommentRepository squadCommentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void add(Long memberId, Long squadId, String content) {
        Squad squad = squadAccessor.getById(squadId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrewId());
        SquadComment comment = squadCommentRepository.save(SquadComment.create(content, squad, me.getMember()));
        eventPublisher.publishEvent(new CommentAdded(memberId, comment.getId()));
    }

    public void addReply(Long memberId, Long squadId, Long parentId, String content) {
        SquadComment parent = squadCommentAccessor.getWithSquadById(parentId);
        SquadCommentPolicy.ensureMatchSquad(parent, squadId);
        SquadCommentPolicy.ensureCommentIsAlive(parent);
        SquadCommentPolicy.ensureCommentIsParent(parent);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, parent.getSquad().getCrewId());
        SquadComment reply = squadCommentRepository.save(SquadComment.createReply(parent, content, parent.getSquad(), me.getMember()));
        eventPublisher.publishEvent(new CommentReplyAdded(parentId, memberId, reply.getId()));
    }

    public void update(Long memberId, Long squadId, Long commentId, String content) {
        SquadComment comment = squadCommentAccessor.getById(commentId);
        SquadCommentPolicy.ensureMatchSquad(comment, squadId);
        SquadCommentPolicy.ensureCommentIsAlive(comment);
        SquadCommentPolicy.ensureMatchWriter(comment, memberId);
        comment.update(content);
    }

    public void delete(Long memberId, Long squadId, Long commentId) {
        SquadComment comment = squadCommentAccessor.getById(commentId);
        SquadCommentPolicy.ensureMatchSquad(comment, squadId);
        SquadMember me = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        SquadCommentPolicy.ensureDeletable(me, comment);
        comment.delete();
    }
}
