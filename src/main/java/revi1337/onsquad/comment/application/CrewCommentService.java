package revi1337.onsquad.comment.application;

import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collector;
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
    public CommentDto addComment(CreateCommentDto dto, Long memberId) {
        return crewRepository.findByName(new Name(dto.crewName()))
                .flatMap(crew -> persistCommentAndCreateDto(dto, memberId, crew))
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, dto.crewName()));
    }

    private Optional<CommentDto> persistCommentAndCreateDto(CreateCommentDto dto, Long memberId, Crew crew) {
        return memberRepository.findById(memberId)
                .map(member -> persistCommentAndBuildDto(Comment.of(dto.content(), crew, member), member));
    }

    private CommentDto persistCommentAndBuildDto(Comment comment, Member member) {
        Comment persistComment = commentRepository.save(comment);
        return CommentDto.from(persistComment, member);
    }

    public List<CommentsDto> findComments(String crewName) {
        List<Comment> comments = commentRepository.findCommentsByCrewName(new Name(crewName));
        return comments.stream()
                .collect(Collectors.collectingAndThen(
                        convertToHashMap(),
                        buildCommentDtos(comments)
                ));
    }

    private Collector<Comment, ?, LinkedHashMap<Long, CommentsDto>> convertToHashMap() {
        return Collectors.toMap(
                Comment::getId,
                CommentsDto::from,
                (dto1, dto2) -> dto2,
                LinkedHashMap::new
        );
    }

    private Function<LinkedHashMap<Long, CommentsDto>, List<CommentsDto>> buildCommentDtos(List<Comment> comments) {
        return commentMap -> {
            separateCommentAndReplies(comments, commentMap);
            return commentMap.values().stream()
                    .filter(dto -> dto.parentCommentId() == null)
                    .collect(Collectors.toList());
        };
    }

    private void separateCommentAndReplies(List<Comment> comments, Map<Long, CommentsDto> commentMap) {
        comments.forEach(comment -> {
            if (comment.getParent() != null) {
                CommentsDto childDto = commentMap.get(comment.getId());
                CommentsDto parentDto = commentMap.get(comment.getParent().getId());
                parentDto.replies().add(childDto);
            }
        });
    }

    @Transactional
    public CommentDto addCommentReply(CreateCommentReplyDto dto, Long memberId) {
        return commentRepository.findCommentById(dto.parentCommentId())
                .flatMap(comment -> persistCommentReplyAndCreateDtoIfParent(dto, memberId, comment))
                .orElseThrow(() -> new CommentBusinessException.NotFoundById(NOTFOUND_COMMENT, dto.parentCommentId()));
    }

    private Optional<CommentDto> persistCommentReplyAndCreateDtoIfParent(CreateCommentReplyDto dto, Long memberId, Comment comment) {
        validateParentComment(dto, comment);
        return memberRepository.findById(memberId)
                .map(member -> persistCommentAndBuildDto(Comment.forReply(comment, dto.content(), comment.getCrew(), member), member));
    }

    private void validateParentComment(CreateCommentReplyDto dto, Comment comment) {
        if (comment.getParent() != null) {
            throw new CommentBusinessException.NotParent(NOT_PARENT, dto.parentCommentId());
        }

        if (!comment.getCrew().getName().equals(new Name(dto.crewName()))) {
            throw new CommentBusinessException.NotFoundCrewComment(NOTFOUND_CREW_COMMENT, dto.crewName(), dto.parentCommentId());
        }
    }
}
