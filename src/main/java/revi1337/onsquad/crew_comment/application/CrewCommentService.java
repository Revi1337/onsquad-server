package revi1337.onsquad.crew_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_comment.domain.CrewComment;
import revi1337.onsquad.crew_comment.domain.CrewCommentRepository;
import revi1337.onsquad.crew_comment.dto.CreateCrewCommentDto;
import revi1337.onsquad.crew_comment.dto.CreateCrewCommentReplyDto;
import revi1337.onsquad.crew_comment.dto.CrewCommentDto;
import revi1337.onsquad.crew_comment.dto.CrewCommentsDto;
import revi1337.onsquad.crew_comment.error.exception.CrewCommentBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static revi1337.onsquad.crew_comment.error.CrewCommentErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewCommentService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewCommentRepository crewCommentRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Transactional
    public CrewCommentDto addComment(Long crewId, CreateCrewCommentDto dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOTFOUND, memberId));
        Crew crew = crewRepository.getById(crewId);
        CrewComment persistComment = crewCommentRepository.save(CrewComment.forCrew(dto.content(), crew, member));

        return CrewCommentDto.from(persistComment, member);
    }

    @Transactional
    public CrewCommentDto addCommentReply(Long crewId, CreateCrewCommentReplyDto dto, Long memberId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        CrewComment parentComment = crewCommentRepository.getById(dto.parentCommentId());
        validateParentComment(crewId, dto, parentComment);
        CrewComment childComment = crewCommentRepository.save(
                CrewComment.replyForCrew(parentComment, dto.content(), parentComment.getCrew(), crewMember.getMember())
        );

        return CrewCommentDto.from(childComment, crewMember.getMember());
    }

    public List<CrewCommentsDto> findComments(Long crewId, Pageable pageable, Integer childSize) {
        List<CrewComment> parentComments = crewCommentRepository.findLimitedParentCommentsByCrewId(crewId, pageable);
        List<Long> parentCommentIds = collectParentIds(parentComments);
        List<CrewComment> childComments = crewCommentRepository.findLimitedChildCommentsByParentIdIn(parentCommentIds, childSize);

        Map<Long, CrewCommentsDto> mappedParentComment = convertAndMapByParentId(parentComments);
        linkChildCommentsToParent(childComments, mappedParentComment);

        return parentComments.stream()
                .map(comment -> mappedParentComment.get(comment.getId()))
                .toList();
    }

    public List<CrewCommentsDto> findAllComments(Long crewId) {
        List<CrewComment> comments = crewCommentRepository.findCommentsWithMemberByCrewId(crewId);
        return convertToCommentsDtoAndMappedWithParent(comments);
    }

    private void validateParentComment(Long crewId, CreateCrewCommentReplyDto dto, CrewComment comment) {
        if (comment.getParent() != null) {
            throw new CrewCommentBusinessException.NotParent(NOT_PARENT, dto.parentCommentId());
        }

        if (!comment.getCrew().getId().equals(crewId)) {
            throw new CrewCommentBusinessException.NotFoundCrewComment(NOTFOUND_CREW_COMMENT, crewId, dto.parentCommentId());
        }
    }

    private Map<Long, CrewCommentsDto> convertAndMapByParentId(List<CrewComment> parentComments) {
        return parentComments.stream()
                .map(CrewCommentsDto::from)
                .collect(Collectors.toMap(CrewCommentsDto::commentId, Function.identity()));
    }

    private void linkChildCommentsToParent(List<CrewComment> childComments, Map<Long, CrewCommentsDto> parentCommentMap) {
        childComments.forEach(childComment -> {
            CrewCommentsDto childDto = CrewCommentsDto.from(childComment);
            CrewCommentsDto parentDto = parentCommentMap.get(childDto.parentCommentId());
            parentDto.replies().add(childDto);
        });
    }

    private List<Long> collectParentIds(List<CrewComment> parentComments) {
        return parentComments.stream()
                .map(CrewComment::getId)
                .toList();
    }

    private List<CrewCommentsDto> convertToCommentsDtoAndMappedWithParent(List<CrewComment> comments) {
        List<CrewCommentsDto> commentList = new ArrayList<>();
        Map<Long, CrewCommentsDto> hashMap = new HashMap<>();
        comments.forEach(comment -> {
            CrewCommentsDto commentDto = CrewCommentsDto.from(comment);
            hashMap.put(commentDto.commentId(), commentDto);
            if (comment.getParent() != null) {
                hashMap.get(comment.getParent().getId()).replies().add(commentDto);
            } else {
                commentList.add(commentDto);
            }
        });

        return commentList;
    }
}
