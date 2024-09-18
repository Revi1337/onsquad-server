package revi1337.onsquad.crew_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_comment.domain.CrewComment;
import revi1337.onsquad.crew_comment.domain.CrewCommentRepository;
import revi1337.onsquad.crew_comment.dto.CreateCrewCommentDto;
import revi1337.onsquad.crew_comment.dto.CreateCrewCommentReplyDto;
import revi1337.onsquad.crew_comment.dto.CrewCommentDto;
import revi1337.onsquad.crew_comment.dto.CrewCommentsDto;
import revi1337.onsquad.crew_comment.error.exception.CrewCommentBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;
import static revi1337.onsquad.crew_comment.error.CrewCommentErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewCommentService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewCommentRepository crewCommentRepository;

    @Transactional
    public CrewCommentDto addComment(String crewName, CreateCrewCommentDto dto, Long memberId) {
        return crewRepository.findByName(new Name(crewName))
                .flatMap(crew -> persistCommentAndCreateDto(dto, memberId, crew))
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(NOTFOUND_CREW, crewName));
    }

    private Optional<CrewCommentDto> persistCommentAndCreateDto(CreateCrewCommentDto dto, Long memberId, Crew crew) {
        return memberRepository.findById(memberId)
                .map(member -> persistCommentAndBuildDto(CrewComment.forCrew(dto.content(), crew, member), member));
    }

    private CrewCommentDto persistCommentAndBuildDto(CrewComment comment, Member member) {
        CrewComment persistComment = crewCommentRepository.save(comment);
        return CrewCommentDto.from(persistComment, member);
    }

    @Transactional
    public CrewCommentDto addCommentReply(String crewName, CreateCrewCommentReplyDto dto, Long memberId) {
        return crewCommentRepository.findCommentById(dto.parentCommentId())
                .flatMap(comment -> persistCommentReplyAndCreateDtoIfParent(crewName, dto, memberId, comment))
                .orElseThrow(() -> new CrewCommentBusinessException.NotFoundById(NOTFOUND_COMMENT, dto.parentCommentId()));
    }

    private Optional<CrewCommentDto> persistCommentReplyAndCreateDtoIfParent(String crewName, CreateCrewCommentReplyDto dto, Long memberId, CrewComment comment) {
        validateParentComment(crewName, dto, comment);
        return memberRepository.findById(memberId)
                .map(member -> persistCommentAndBuildDto(CrewComment.replyForCrew(comment, dto.content(), comment.getCrew(), member), member));
    }

    private void validateParentComment(String crewName, CreateCrewCommentReplyDto dto, CrewComment comment) {
        if (comment.getParent() != null) {
            throw new CrewCommentBusinessException.NotParent(NOT_PARENT, dto.parentCommentId());
        }

        if (!comment.getCrew().getName().equals(new Name(crewName))) {
            throw new CrewCommentBusinessException.NotFoundCrewCrewComment(NOTFOUND_CREW_COMMENT, crewName, dto.parentCommentId());
        }
    }

    public List<CrewCommentsDto> findComments(String crewName, Pageable parentPageable, Integer childSize) {
        List<CrewComment> parentComments = crewCommentRepository.findLimitedParentCommentsByCrewName(new Name(crewName), parentPageable);
        Map<Long, CrewCommentsDto> commentDtoMap = convertToCommentsDto(parentComments);
        List<CrewComment> childComments = retrieveChildCommentsByParentIds(childSize, parentComments);

        convertToCommentsDtoAndLinkToParent(childComments, commentDtoMap);
        return parentComments.stream()
                .map(comment -> commentDtoMap.get(comment.getId()))
                .toList();
    }

    private List<CrewComment> retrieveChildCommentsByParentIds(Integer childSize, List<CrewComment> parentComments) {
        List<Long> parentIds = collectParentIds(parentComments);
        return crewCommentRepository.findLimitedChildCommentsByParentIdIn(parentIds, childSize);
    }

    private Map<Long, CrewCommentsDto> convertToCommentsDto(List<CrewComment> parentComments) {
        return parentComments.stream()
                .map(CrewCommentsDto::from)
                .collect(Collectors.toMap(CrewCommentsDto::commentId, Function.identity()));
    }

    private void convertToCommentsDtoAndLinkToParent(List<CrewComment> childComments, Map<Long, CrewCommentsDto> commentDtoMap) {
        childComments.forEach(childComment -> {
            CrewCommentsDto childDto = CrewCommentsDto.from(childComment);
            CrewCommentsDto parentDto = commentDtoMap.get(childComment.getParent().getId());
            parentDto.replies().add(childDto);
        });
    }

    private List<Long> collectParentIds(List<CrewComment> parentComments) {
        return parentComments.stream()
                .map(CrewComment::getId)
                .toList();
    }

    public List<CrewCommentsDto> findAllComments(String crewName) {
        List<CrewComment> comments = crewCommentRepository.findCommentsByCrewName(new Name(crewName));
        return convertToCommentsDtoAndMappedWithParent(comments);
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
