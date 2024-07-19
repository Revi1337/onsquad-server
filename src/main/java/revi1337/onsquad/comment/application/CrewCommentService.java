package revi1337.onsquad.comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.comment.domain.Comment;
import revi1337.onsquad.comment.domain.CommentRepository;
import revi1337.onsquad.comment.dto.CommentsDto;
import revi1337.onsquad.comment.dto.CreateCommentDto;
import revi1337.onsquad.comment.dto.CommentDto;
import revi1337.onsquad.comment.dto.CreateCommentReplyDto;
import revi1337.onsquad.comment.error.exception.CommentBusinessException;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static revi1337.onsquad.comment.error.CommentErrorCode.*;
import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewCommentService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentDto addComment(String crewName, CreateCommentDto dto, Long memberId) {
        return crewRepository.findByName(new Name(crewName))
                .flatMap(crew -> persistCommentAndCreateDto(dto, memberId, crew))
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, crewName));
    }

    private Optional<CommentDto> persistCommentAndCreateDto(CreateCommentDto dto, Long memberId, Crew crew) {
        return memberRepository.findById(memberId)
                .map(member -> persistCommentAndBuildDto(Comment.of(dto.content(), crew, member), member));
    }

    private CommentDto persistCommentAndBuildDto(Comment comment, Member member) {
        Comment persistComment = commentRepository.save(comment);
        return CommentDto.from(persistComment, member);
    }

    @Transactional
    public CommentDto addCommentReply(String crewName, CreateCommentReplyDto dto, Long memberId) {
        return commentRepository.findCommentById(dto.parentCommentId())
                .flatMap(comment -> persistCommentReplyAndCreateDtoIfParent(crewName, dto, memberId, comment))
                .orElseThrow(() -> new CommentBusinessException.NotFoundById(NOTFOUND_COMMENT, dto.parentCommentId()));
    }

    private Optional<CommentDto> persistCommentReplyAndCreateDtoIfParent(String crewName, CreateCommentReplyDto dto, Long memberId, Comment comment) {
        validateParentComment(crewName, dto, comment);
        return memberRepository.findById(memberId)
                .map(member -> persistCommentAndBuildDto(Comment.forReply(comment, dto.content(), comment.getCrew(), member), member));
    }

    private void validateParentComment(String crewName, CreateCommentReplyDto dto, Comment comment) {
        if (comment.getParent() != null) {
            throw new CommentBusinessException.NotParent(NOT_PARENT, dto.parentCommentId());
        }

        if (!comment.getCrew().getName().equals(new Name(crewName))) {
            throw new CommentBusinessException.NotFoundCrewComment(NOTFOUND_CREW_COMMENT, crewName, dto.parentCommentId());
        }
    }

    public List<CommentsDto> findComments(String crewName, Pageable parentPageable, Integer childSize) {
        List<Comment> parentComments = commentRepository.findLimitedParentCommentsByCrewName(new Name(crewName), parentPageable);
        Map<Long, CommentsDto> commentDtoMap = convertToCommentsDto(parentComments);
        List<Comment> childComments = retrieveChildCommentsByParentIds(childSize, parentComments);

        convertToCommentsDtoAndLinkToParent(childComments, commentDtoMap);
        return parentComments.stream()
                .map(comment -> commentDtoMap.get(comment.getId()))
                .toList();
    }

    private List<Comment> retrieveChildCommentsByParentIds(Integer childSize, List<Comment> parentComments) {
        List<Long> parentIds = collectParentIds(parentComments);
        return commentRepository.findLimitedChildCommentsByParentIdIn(parentIds, childSize);
    }

    private Map<Long, CommentsDto> convertToCommentsDto(List<Comment> parentComments) {
        return parentComments.stream()
                .map(CommentsDto::from)
                .collect(Collectors.toMap(CommentsDto::commentId, Function.identity()));
    }

    private void convertToCommentsDtoAndLinkToParent(List<Comment> childComments, Map<Long, CommentsDto> commentDtoMap) {
        childComments.forEach(childComment -> {
            CommentsDto childDto = CommentsDto.from(childComment);
            CommentsDto parentDto = commentDtoMap.get(childComment.getParent().getId());
            parentDto.replies().add(childDto);
        });
    }

    private List<Long> collectParentIds(List<Comment> parentComments) {
        return parentComments.stream()
                .map(Comment::getId)
                .toList();
    }

    public List<CommentsDto> findAllComments(String crewName) {
        List<Comment> comments = commentRepository.findCommentsByCrewName(new Name(crewName));
        return convertToCommentsDtoAndMappedWithParent(comments);
    }

    private List<CommentsDto> convertToCommentsDtoAndMappedWithParent(List<Comment> comments) {
        List<CommentsDto> commentList = new ArrayList<>();
        Map<Long, CommentsDto> hashMap = new HashMap<>();
        comments.forEach(comment -> {
            CommentsDto commentDto = CommentsDto.from(comment);
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
