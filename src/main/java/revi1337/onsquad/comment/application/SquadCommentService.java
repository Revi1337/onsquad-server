package revi1337.onsquad.comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.comment.domain.Comment;
import revi1337.onsquad.comment.domain.CommentRepository;
import revi1337.onsquad.comment.dto.CommentDto;
import revi1337.onsquad.comment.dto.CommentsDto;
import revi1337.onsquad.comment.dto.CreateCommentDto;
import revi1337.onsquad.comment.dto.CreateCommentReplyDto;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadJpaRepository;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;

import java.util.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadCommentService {

    private final CrewRepository crewRepository;
    private final SquadJpaRepository squadJpaRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    /**
     * squad 랑 crew 를 같이 조회 (쿼리) (O)
     *      --> 있으면 squad 가 속한 crew 가 올바른지 검증 (app) (O)
     *          --> 검증되면 member 가 있나 확인. (O)
     *              --> member 가 있으면 comment 를 저장. (O)
     *              --> member 가 없으면 오류 (O)
     *          --> 검증되지 않으면 직접 crew 를 조회 (O)
     *              --> crew 가 존재하면 squad 는 crew 에 속해있지 않다는 오류 (O)
     *              --> crew 가 존재하지 않으면 crew 가 없다는 404 (O)
     *      --> 없으면 Squad 가 없다는 404 (O)
     */
    @Transactional
    public CommentDto addComment(String crewName, Long squadId, CreateCommentDto dto, Long memberId) {
        return squadJpaRepository.findSquadByIdWithCrew(squadId)
                .map(squad -> persistSquadCommentIfValid(crewName, dto, memberId, squad))
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOTFOUND));
    }

    private CommentDto persistSquadCommentIfValid(String crewName, CreateCommentDto dto, Long memberId, Squad squad) {
        if (!validateCrewInSquad(crewName, squad)) {
            retrieveCrewAndAnalyzeError(crewName);
        }

        return persistCommentIfMemberExists(dto, memberId, squad);
    }

    private void retrieveCrewAndAnalyzeError(String crewName) {
        crewRepository.findByName(new Name(crewName))
                .ifPresentOrElse(
                        crew -> { throw new SquadBusinessException.NotInCrew(SquadErrorCode.NOT_IN_CREW, crewName); },
                        () -> { throw new CrewBusinessException.NotFoundByName(CrewErrorCode.NOTFOUND_CREW, crewName); }
                );
    }

    private boolean validateCrewInSquad(String crewName, Squad squad) {
        return squad.getCrew().getName().equals(new Name(crewName));
    }

    private CommentDto persistCommentIfMemberExists(CreateCommentDto dto, Long memberId, Squad squad) {
        return memberRepository.findById(memberId)
                .map(member -> persistCommentAndConvertDto(dto, squad, member))
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOTFOUND, memberId));
    }

    private CommentDto persistCommentAndConvertDto(CreateCommentDto dto, Squad squad, Member member) {
        Comment comment = Comment.forSquad(dto.content(), squad.getCrew(), member);
        commentRepository.save(comment);
        return CommentDto.from(comment, member);
    }

    @Transactional
    public CommentDto addCommentReply(String crewName, Long squadId, CreateCommentReplyDto dto, Long memberId) {
        return null;
    }

    public List<CommentsDto> findComments(String crewName, Long squadId, Pageable parentPageable, Integer childSize) {
        return List.of();
    }

    public List<CommentsDto> findAllComments(String crewName, Long squadId) {
        return List.of();
    }
}
