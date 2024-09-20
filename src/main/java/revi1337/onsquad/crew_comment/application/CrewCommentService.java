package revi1337.onsquad.crew_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_comment.application.dto.CrewCommentDto;
import revi1337.onsquad.crew_comment.domain.CrewComment;
import revi1337.onsquad.crew_comment.domain.CrewCommentRepository;
import revi1337.onsquad.crew_comment.application.dto.CreateCrewCommentDto;
import revi1337.onsquad.crew_comment.application.dto.CreateCrewCommentReplyDto;
import revi1337.onsquad.crew_comment.domain.dto.SimpleCrewCommentDto;
import revi1337.onsquad.crew_comment.error.exception.CrewCommentBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;

import java.util.*;

import static revi1337.onsquad.crew_comment.error.CrewCommentErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewCommentService {

    private final CrewCommentRepository crewCommentRepository;
    private final CrewMemberRepository crewMemberRepository;

    public SimpleCrewCommentDto addComment(Long memberId, Long crewId, CreateCrewCommentDto dto) {
        CrewMember crewMember = crewMemberRepository.getWithMemberByCrewIdAndMemberId(crewId, memberId);
        CrewComment persistComment = crewCommentRepository.save(
                CrewComment.create(dto.content(), crewMember.getCrew(), crewMember)
        );

        return SimpleCrewCommentDto.from(persistComment, crewMember.getMember());
    }

    public SimpleCrewCommentDto addCommentReply(Long memberId, Long crewId, CreateCrewCommentReplyDto dto) {
        CrewMember crewMember = crewMemberRepository.getWithMemberByCrewIdAndMemberId(crewId, memberId);
        CrewComment parentComment = crewCommentRepository.getById(dto.parentCommentId());
        validateParentComment(crewId, dto, parentComment);
        CrewComment childComment = crewCommentRepository.save(
                CrewComment.createReply(parentComment, dto.content(), parentComment.getCrew(), crewMember)
        );

        return SimpleCrewCommentDto.from(childComment, crewMember.getMember());
    }

    public List<CrewCommentDto> findParentComments(Long crewId, Pageable pageable, Integer childSize) {
        return crewCommentRepository.findLimitedCommentsBothOfParentsAndChildren(crewId, pageable, childSize).stream()
                .map(CrewCommentDto::from)
                .toList();
    }

    public List<CrewCommentDto> findMoreChildComments(Long crewId, Long parentId, Pageable pageable) {
        return crewCommentRepository.findChildComments(crewId, parentId, pageable).stream()
                .map(CrewCommentDto::from)
                .toList();
    }

    public List<CrewCommentDto> findAllComments(Long crewId) {
        return crewCommentRepository.findAllWithMemberByCrewId(crewId).stream()
                .map(CrewCommentDto::from)
                .toList();
    }

    private void validateParentComment(Long crewId, CreateCrewCommentReplyDto dto, CrewComment comment) {
        if (comment.getParent() != null) {
            throw new CrewCommentBusinessException.NotParent(NOT_PARENT, dto.parentCommentId());
        }

        if (!comment.getCrew().getId().equals(crewId)) {
            throw new CrewCommentBusinessException.NotFoundCrewComment(NOTFOUND_CREW_COMMENT, crewId, dto.parentCommentId());
        }
    }
}
