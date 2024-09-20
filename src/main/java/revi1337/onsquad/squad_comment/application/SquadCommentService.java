package revi1337.onsquad.squad_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_comment.application.dto.CreateSquadCommentDto;
import revi1337.onsquad.squad_comment.application.dto.SquadCommentDto;
import revi1337.onsquad.squad_comment.domain.SquadComment;
import revi1337.onsquad.squad_comment.domain.SquadCommentRepository;
import revi1337.onsquad.squad_comment.domain.dto.SimpleSquadCommentDto;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentBusinessException;

import java.util.List;

import static revi1337.onsquad.crew_comment.error.CrewCommentErrorCode.NOTFOUND_CREW_COMMENT;
import static revi1337.onsquad.crew_comment.error.CrewCommentErrorCode.NOT_PARENT;
import static revi1337.onsquad.squad.error.SquadErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadCommentService {

    private final SquadCommentRepository squadCommentRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadRepository squadRepository;

    public SimpleSquadCommentDto addComment(Long memberId, Long crewId, Long squadId, CreateSquadCommentDto dto) {
        CrewMember crewMember = crewMemberRepository.getWithMemberByCrewIdAndMemberId(crewId, memberId);
        Squad squad = squadRepository.getById(squadId);
        validateSquadInCrew(crewId, squad);
        if (dto.parentId() == null) {
            return persistComment(dto, squad, crewMember);
        }

        return persistCommentReply(squadId, dto, squad, crewMember);
    }

    public List<SquadCommentDto> findParentComments(Long memberId, Long crewId, Long squadId, Pageable pageable, Integer childSize) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return squadCommentRepository.findLimitedCommentsBothOfParentsAndChildren(squadId, pageable, childSize).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    public List<SquadCommentDto> findMoreChildComments(Long memberId, Long crewId, Long squadId, Long parentId, Pageable pageable) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return squadCommentRepository.findChildComments(squadId, parentId, pageable).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    public List<SquadCommentDto> findAllComments(Long memberId, Long crewId, Long squadId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return squadCommentRepository.findAllWithMemberByCrewId(squadId).stream()
                .map(SquadCommentDto::from)
                .toList();
    }

    private SimpleSquadCommentDto persistComment(CreateSquadCommentDto dto, Squad squad, CrewMember crewMember) {
        SquadComment rawComment = SquadComment.create(dto.content(), squad, crewMember);
        SquadComment persistComment = squadCommentRepository.save(rawComment);

        return SimpleSquadCommentDto.from(persistComment, crewMember.getMember());
    }

    private SimpleSquadCommentDto persistCommentReply(Long squadId, CreateSquadCommentDto dto, Squad squad, CrewMember crewMember) {
        SquadComment parentComment = squadCommentRepository.getById(dto.parentId());
        validateParentComment(squadId, dto, parentComment);
        SquadComment rawCommentReply = SquadComment.createReply(parentComment, dto.content(), squad, crewMember);
        SquadComment persistCommentReply = squadCommentRepository.save(rawCommentReply);

        return SimpleSquadCommentDto.from(persistCommentReply, crewMember.getMember());
    }

    private void validateSquadInCrew(Long crewId, Squad squad) {
        if (!squad.getCrew().getId().equals(crewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }
    }

    private void validateParentComment(Long squadId, CreateSquadCommentDto dto, SquadComment comment) {
        if (comment.getParent() != null) {
            throw new SquadCommentBusinessException.NotParent(NOT_PARENT, dto.parentId());
        }

        if (!comment.getSquad().getId().equals(squadId)) {
            throw new SquadCommentBusinessException.NotFoundSquadComment(NOTFOUND_CREW_COMMENT, squadId, dto.parentId());
        }
    }
}
