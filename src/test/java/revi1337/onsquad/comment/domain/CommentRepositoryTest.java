package revi1337.onsquad.comment.domain;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.blazebit.persistence.querydsl.JPQLNextExpressions;
import com.blazebit.persistence.querydsl.NamedWindow;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLExpressions;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.comment.dto.CommentsDto;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.support.PersistenceLayerTestSupport;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.blazebit.persistence.querydsl.JPQLNextExpressions.select;
import static revi1337.onsquad.comment.domain.QComment.*;
import static revi1337.onsquad.crew.domain.QCrew.*;
import static revi1337.onsquad.member.domain.QMember.*;

class CommentRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired private MemberRepository memberRepository;
    @Autowired private CrewRepository crewRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private EntityManager entityManager;
    @Autowired private JPAQueryFactory jpaQueryFactory;
    @Autowired private CriteriaBuilderFactory cbf;
    @Autowired private BlazeJPAQueryFactory blazeQueryFactory;

//    @BeforeEach
//    public void testEntity() {
//        blazeQueryFactory = new BlazeJPAQueryFactory(entityManager, cbf);
//    }

    @Test
    @DisplayName("대댓글이 잘 작성되는지 확인한다.")
    public void findCommentsByCrewName() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 1", crew1, member1);
        Comment comment2 = Comment.of("댓글 2", crew1, member1);
        Comment comment3 = Comment.of("댓글 3", crew1, member1);
        Comment comment4 = Comment.of("댓글 4", crew1, member1);
        memberRepository.saveAll(List.of(member1, member2));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member1);
        commentRepository.saveAll(List.of(reply1));

        List<Comment> replies = comment1.getReplies();
        for (Comment reply : replies) {
            System.out.println("reply = " + reply);
        }
    }

    @Test
    @DisplayName("댓글과 대댓글을 페이징 없이 모두 조회한다. v1 218000 (v1)")
    public void findComments() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Member member3 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 4", crew1, member1);
        Comment comment2 = Comment.of("댓글 3", crew1, member1);
        Comment comment3 = Comment.of("댓글 2", crew1, member1);
        Comment comment4 = Comment.of("댓글 1", crew1, member1);
        memberRepository.saveAll(List.of(member1, member2, member3));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member1);
        Comment reply2 = Comment.forReply(comment1, "대댓글 2", crew1, member2);
        Comment reply3 = Comment.forReply(comment1, "대댓글 3", crew1, member2);
        Comment reply4 = Comment.forReply(comment2, "대댓글 4", crew1, member2);
        Comment reply5 = Comment.forReply(comment2, "대댓글 5", crew1, member1);
        Comment reply6 = Comment.forReply(comment2, "대댓글 6", crew1, member1);
        Comment reply7 = Comment.forReply(comment3, "대댓글 7", crew1, member2);
        Comment reply8 = Comment.forReply(comment3, "대댓글 8", crew1, member1);
        Comment reply9 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply10 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply11 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply12 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply13 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply14 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply15 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply16 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply17 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply18 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply19 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply20 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply21 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply22 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply23 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply24 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply25 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply26 = Comment.forReply(comment3, "대댓글 9", crew1, member1);

        commentRepository.saveAll(
                List.of(
                        reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                        reply11, reply12, reply13, reply14, reply15, reply16, reply17, reply18, reply19,
                        reply20, reply21, reply22, reply23, reply24, reply25, reply26
                )
        );

        List<Comment> findComments = commentRepository.findCommentsByCrewName(new Name("크루 이름 1"));

        entityManager.flush();
        entityManager.clear();

        LocalDateTime start = LocalDateTime.now();
        List<CommentsDto> commentList = new ArrayList<>();
        Map<Long, CommentsDto> hashMap = new HashMap<>();
        findComments.forEach(comment -> {
            CommentsDto commentDto = CommentsDto.from(comment);
            hashMap.put(commentDto.commentId(), commentDto);
            if (comment.getParent() != null) {
                hashMap.get(comment.getParent().getId()).replies().add(commentDto);
            } else {
                commentList.add(commentDto);
            }
        });
        LocalDateTime finished = LocalDateTime.now();
        System.out.println(Duration.between(start, finished).toNanos());
        for (CommentsDto commentsDto : commentList) {
            System.out.println("commentsDto = " + commentsDto);
        }
    }

    @Test
    @DisplayName("댓글과 대댓글을 페이징 없이 모두 조회한다. v2 12612000 (v2)")
    public void findComments2() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Member member3 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 4", crew1, member1);
        Comment comment2 = Comment.of("댓글 3", crew1, member1);
        Comment comment3 = Comment.of("댓글 2", crew1, member1);
        Comment comment4 = Comment.of("댓글 1", crew1, member1);
        memberRepository.saveAll(List.of(member1, member2, member3));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member1);
        Comment reply2 = Comment.forReply(comment1, "대댓글 2", crew1, member2);
        Comment reply3 = Comment.forReply(comment1, "대댓글 3", crew1, member2);
        Comment reply4 = Comment.forReply(comment2, "대댓글 4", crew1, member2);
        Comment reply5 = Comment.forReply(comment2, "대댓글 5", crew1, member1);
        Comment reply6 = Comment.forReply(comment2, "대댓글 6", crew1, member1);
        Comment reply7 = Comment.forReply(comment3, "대댓글 7", crew1, member2);
        Comment reply8 = Comment.forReply(comment3, "대댓글 8", crew1, member1);
        Comment reply9 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply10 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply11 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply12 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply13 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply14 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply15 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply16 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply17 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply18 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply19 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply20 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply21 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply22 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply23 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply24 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply25 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply26 = Comment.forReply(comment3, "대댓글 9", crew1, member1);

        commentRepository.saveAll(
                List.of(
                        reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                        reply11, reply12, reply13, reply14, reply15, reply16, reply17, reply18, reply19,
                        reply20, reply21, reply22, reply23, reply24, reply25, reply26
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<Comment> findComments = commentRepository.findCommentsByCrewName(new Name("크루 이름 1"));

        LocalDateTime start = LocalDateTime.now();
        Map<Long, CommentsDto> commentMap = findComments.stream()
                .map(CommentsDto::from)
                .collect(Collectors.toMap(
                        CommentsDto::commentId,
                        Function.identity()
                ));

        List<CommentsDto> comments = findComments.stream()
                .map(comment -> {
                    CommentsDto commentDto = commentMap.get(comment.getId());
                    if (comment.getParent() != null) {
                        commentMap.get(comment.getParent().getId()).replies().add(commentDto);
                        return null; // 대댓글은 최상위 리스트에 포함되지 않도록 null 반환
                    } else {
                        return commentDto;
                    }
                })
                .filter(Objects::nonNull) // null 값 필터링
                .toList();

        LocalDateTime finished = LocalDateTime.now();
        System.out.println(Duration.between(start, finished).toNanos());
        for (CommentsDto comment : comments) {
            System.out.println("comment = " + comment);
        }
    }

    @Test
    @DisplayName("댓글과 대댓글을 페이징 없이 모두 조회한다. 737000 나노초 (v3)")
    public void findComments3() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Member member3 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 4", crew1, member1);
        Comment comment2 = Comment.of("댓글 3", crew1, member1);
        Comment comment3 = Comment.of("댓글 2", crew1, member1);
        Comment comment4 = Comment.of("댓글 1", crew1, member1);
        memberRepository.saveAll(List.of(member1, member2, member3));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member1);
        Comment reply2 = Comment.forReply(comment1, "대댓글 2", crew1, member2);
        Comment reply3 = Comment.forReply(comment1, "대댓글 3", crew1, member2);
        Comment reply4 = Comment.forReply(comment2, "대댓글 4", crew1, member2);
        Comment reply5 = Comment.forReply(comment2, "대댓글 5", crew1, member1);
        Comment reply6 = Comment.forReply(comment2, "대댓글 6", crew1, member1);
        Comment reply7 = Comment.forReply(comment3, "대댓글 7", crew1, member2);
        Comment reply8 = Comment.forReply(comment3, "대댓글 8", crew1, member1);
        Comment reply9 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply10 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply11 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply12 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply13 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply14 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply15 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply16 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply17 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply18 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply19 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply20 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply21 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply22 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply23 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply24 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply25 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply26 = Comment.forReply(comment3, "대댓글 9", crew1, member1);

        commentRepository.saveAll(
                List.of(
                        reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                        reply11, reply12, reply13, reply14, reply15, reply16, reply17, reply18, reply19,
                        reply20, reply21, reply22, reply23, reply24, reply25, reply26
                )
        );

        List<Comment> findComments = commentRepository.findCommentsByCrewName(new Name("크루 이름 1"));

        entityManager.flush();
        entityManager.clear();

        LocalDateTime start = LocalDateTime.now();
        List<CommentsDto> comments = findComments.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                Comment::getId,
                                CommentsDto::from,
                                (a, b) -> b,
                                LinkedHashMap::new // 순서 보장을 위해 LinkedHashMap 사용
                        ),
                        commentMap -> {
                            findComments.forEach(comment -> {
                                if (comment.getParent() != null) {
                                    CommentsDto childDto = commentMap.get(comment.getId());
                                    CommentsDto parentDto = commentMap.get(comment.getParent().getId());
                                    parentDto.replies().add(childDto);
                                }
                            });
                            return commentMap.values().stream()
                                    .filter(dto -> dto.parentCommentId() == null) // 최상위 댓글만 필터링
                                    .collect(Collectors.toList());
                        }
                ));

        LocalDateTime finished = LocalDateTime.now();
        System.out.println(Duration.between(start, finished).toNanos());
        for (CommentsDto comment : comments) {
            System.out.println("comment = " + comment);
        }
    }

    @Test
    @DisplayName("부모 댓글들 조회 후 --> 그 id 들로 자식댓글들을 찾아오는 방식 not transform")
    public void transformTest() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Member member3 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 4", crew1, member1);
        Comment comment2 = Comment.of("댓글 3", crew1, member1);
        Comment comment3 = Comment.of("댓글 2", crew1, member1);
        Comment comment4 = Comment.of("댓글 1", crew1, member1);
        memberRepository.saveAll(List.of(member1, member2, member3));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member1);
        Comment reply2 = Comment.forReply(comment1, "대댓글 2", crew1, member2);
        Comment reply3 = Comment.forReply(comment1, "대댓글 3", crew1, member2);
        Comment reply4 = Comment.forReply(comment2, "대댓글 4", crew1, member2);
        Comment reply5 = Comment.forReply(comment2, "대댓글 5", crew1, member1);
        Comment reply6 = Comment.forReply(comment2, "대댓글 6", crew1, member1);
        Comment reply7 = Comment.forReply(comment3, "대댓글 7", crew1, member2);
        Comment reply8 = Comment.forReply(comment3, "대댓글 8", crew1, member1);
        Comment reply9 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply10 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply11 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply12 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply13 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply14 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply15 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply16 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply17 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply18 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply19 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply20 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply21 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply22 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply23 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply24 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply25 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply26 = Comment.forReply(comment3, "대댓글 9", crew2, member1);

        commentRepository.saveAll(
                List.of(
                        reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                        reply11, reply12, reply13, reply14, reply15, reply16, reply17, reply18, reply19,
                        reply20, reply21, reply22, reply23, reply24, reply25, reply26
                )
        );

        entityManager.flush();
        entityManager.clear();

        LocalDateTime st = LocalDateTime.now();
        List<Comment> parentComments = commentRepository.findParentCommentsByCrewName(new Name("크루 이름 1"));
        List<Long> parentIds = parentComments.stream()
                .map(Comment::getId)
                .toList();

        List<Comment> childComments = commentRepository.findChildCommentsByParentIdIn(parentIds);

        // Comment 엔티티를 CommentsDto로 변환
        Map<Long, CommentsDto> commentDtoMap = parentComments.stream()
                .map(CommentsDto::from)
                .collect(Collectors.toMap(CommentsDto::commentId, Function.identity()));

        // 자식 댓글을 CommentsDto로 변환하고 부모 댓글에 추가
        childComments.forEach(childComment -> {
            CommentsDto childDto = CommentsDto.from(childComment);
            CommentsDto parentDto = commentDtoMap.get(childComment.getParent().getId());
            parentDto.replies().add(childDto);
        });

        List<CommentsDto> commentDtos = parentComments.stream()
                .map(comment -> commentDtoMap.get(comment.getId()))
                .toList();
        LocalDateTime end = LocalDateTime.now();
        System.out.println(Duration.between(st, end).toNanos());
    }

    @Test
    @DisplayName("부모 댓글들 조회 후 --> 그 id 들로 자식댓글들을 찾아오는 방식 transform")
    @Disabled
    public void transformTest2() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Member member3 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 4", crew1, member1);
        Comment comment2 = Comment.of("댓글 3", crew1, member1);
        Comment comment3 = Comment.of("댓글 2", crew1, member1);
        Comment comment4 = Comment.of("댓글 1", crew1, member1);
        memberRepository.saveAll(List.of(member1, member2, member3));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member1);
        Comment reply2 = Comment.forReply(comment1, "대댓글 2", crew1, member2);
        Comment reply3 = Comment.forReply(comment1, "대댓글 3", crew1, member2);
        Comment reply4 = Comment.forReply(comment2, "대댓글 4", crew1, member2);
        Comment reply5 = Comment.forReply(comment2, "대댓글 5", crew1, member1);
        Comment reply6 = Comment.forReply(comment2, "대댓글 6", crew1, member1);
        Comment reply7 = Comment.forReply(comment3, "대댓글 7", crew1, member2);
        Comment reply8 = Comment.forReply(comment3, "대댓글 8", crew1, member1);
        Comment reply9 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply10 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply11 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply12 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply13 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply14 = Comment.forReply(comment3, "대댓글 9", crew1, member1);
        Comment reply15 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply16 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply17 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply18 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply19 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply20 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply21 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply22 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply23 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply24 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply25 = Comment.forReply(comment3, "대댓글 9", crew2, member1);
        Comment reply26 = Comment.forReply(comment3, "대댓글 9", crew2, member1);

        commentRepository.saveAll(
                List.of(
                        reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                        reply11, reply12, reply13, reply14, reply15, reply16, reply17, reply18, reply19,
                        reply20, reply21, reply22, reply23, reply24, reply25, reply26
                )
        );

        entityManager.flush();
        entityManager.clear();

        LocalDateTime st = LocalDateTime.now();
        List<Comment> parentComments = commentRepository.findParentCommentsByCrewName(new Name("크루 이름 1"));
        List<Long> parentIds = parentComments.stream()
                .map(Comment::getId)
                .toList();

        Map<Comment, List<Comment>> groupedByParents = commentRepository.findGroupedChildCommentsByParentIdIn(parentIds);

        // 최상위 부모 엔티티를 CommentsDto로 변환
        Map<Long, CommentsDto> commentDtoMap = parentComments.stream()
                .map(CommentsDto::from)
                .collect(Collectors.toMap(CommentsDto::commentId, Function.identity()));

        // 자식 댓글을 CommentsDto로 변환하고 부모 댓글에 추가
        groupedByParents.forEach((parentComment, childComments) -> {
            CommentsDto parentDto = commentDtoMap.get(parentComment.getId());
            List<CommentsDto> childDtos = childComments.stream()
                    .map(CommentsDto::from)
                    .collect(Collectors.toList());
            parentDto.replies().addAll(childDtos);
        });

        List<CommentsDto> commentsDtos = parentComments.stream()
                .map(comment -> commentDtoMap.get(comment.getId()))
                .toList();
        LocalDateTime end = LocalDateTime.now();
        System.out.println(Duration.between(st, end).toNanos());
    }

    @Test
    public void nativeQueryTest() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Member member3 = MemberFactory.defaultMember().build();
        Member member4 = MemberFactory.defaultMember().build();
        Member member5 = MemberFactory.defaultMember().build();
        Member member6 = MemberFactory.defaultMember().build();
        Member member7 = MemberFactory.defaultMember().build();
        Member member8 = MemberFactory.defaultMember().build();
        Member member9 = MemberFactory.defaultMember().build();
        Member member10 = MemberFactory.defaultMember().build();
        Member member11 = MemberFactory.defaultMember().build();
        Member member12 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 5", crew1, member1);
        Comment comment2 = Comment.of("댓글 4", crew1, member2);
        Comment comment3 = Comment.of("댓글 3", crew1, member3);
        Comment comment4 = Comment.of("댓글 2", crew1, member4);
        Comment comment5 = Comment.of("댓글 1", crew1, member5);
        memberRepository.saveAll(List.of(member1, member2, member3, member4, member5, member6, member7, member8, member9, member10, member11, member12));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member6);
        Comment reply2 = Comment.forReply(comment1, "대댓글 2", crew1, member7);
        Comment reply3 = Comment.forReply(comment1, "대댓글 3", crew1, member8);
        Comment reply4 = Comment.forReply(comment1, "대댓글 4", crew1, member9);
        Comment reply5 = Comment.forReply(comment2, "대댓글 5", crew1, member10);
        Comment reply6 = Comment.forReply(comment2, "대댓글 6", crew1, member11);
        Comment reply7 = Comment.forReply(comment2, "대댓글 7", crew1, member12);
        Comment reply8 = Comment.forReply(comment2, "대댓글 8", crew1, member6);
        Comment reply9 = Comment.forReply(comment3, "대댓글 9", crew1, member7);
        Comment reply10 = Comment.forReply(comment3, "대댓글 10", crew1, member8);
        Comment reply11 = Comment.forReply(comment3, "대댓글 11", crew1, member9);
        Comment reply12 = Comment.forReply(comment3, "대댓글 12", crew1, member10);
        Comment reply13 = Comment.forReply(comment4, "대댓글 13", crew1, member11);
        Comment reply14 = Comment.forReply(comment4, "대댓글 14", crew1, member12);
        Comment reply15 = Comment.forReply(comment4, "대댓글 15", crew1, member6);
        Comment reply16 = Comment.forReply(comment4, "대댓글 16", crew1, member7);
        Comment reply17 = Comment.forReply(comment5, "대댓글 17", crew1, member8);
        Comment reply18 = Comment.forReply(comment5, "대댓글 18", crew1, member9);
        Comment reply19 = Comment.forReply(comment5, "대댓글 19", crew1, member10);
        Comment reply20 = Comment.forReply(comment5, "대댓글 20", crew1, member11);
        Comment reply21 = Comment.forReply(comment5, "대댓글 21", crew1, member12);

        commentRepository.saveAll(
                List.of(
                        reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                        reply11, reply12, reply13, reply14, reply15, reply16, reply17, reply18, reply19,
                        reply20, reply21
                )
        );

        entityManager.flush();
        entityManager.clear();


        List<Comment> parentComments = commentRepository.findParentCommentsByCrewName(new Name("크루 이름 1"));
        List<Long> parentIds = parentComments.stream()
                .map(Comment::getId)
                .toList();

        String sql = "SELECT * FROM (" +
                "    SELECT " +
                "        comment.*, " +
                "        ROW_NUMBER() OVER (PARTITION BY comment.parent_id ORDER BY comment.created_at DESC) AS rn " +
                "    FROM comment " +
                "    INNER JOIN member ON comment.member_id = member.id " +
                "    WHERE comment.parent_id IN (:parentIds) " +
                ") AS subquery " +
                "WHERE subquery.rn <= (:childLimit)" +
                "ORDER BY subquery.rn ASC";

        entityManager.flush();
        entityManager.clear();

        List<Comment> resultList = entityManager.createNativeQuery(sql, Comment.class)
                .setParameter("parentIds", parentIds)
                .setParameter("childLimit", 10)
                .getResultList();

        Set<Long> memberIds = resultList.stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("JPQLNextExpressions 테스트 1")
    public void jPQLNextExpressions() {
        List<Tuple> rn = jpaQueryFactory
                .select(comment,
                        JPQLNextExpressions.rowNumber()
                                .over()
                                .partitionBy(comment.parent.id)
                                .orderBy(comment.createdAt.desc())
                                .as("rn")
                )
                .from(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(
                        comment.parent.id.in(1, 2, 3)
                )
//                .having(Expressions.numberPath(Long.class, "rn").loe(10))
//                .orderBy(Expressions.numberPath(Long.class, "rn").asc())
                .fetch();

        for (Tuple tuple : rn) {
            Long l = tuple.get(1, Long.class);
            System.out.println("l = " + l);
        }
    }

    @Test
    @DisplayName("JPQLNextExpressions 테스트 2")
    public void jPQLNextExpressions2() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Member member3 = MemberFactory.defaultMember().build();
        Member member4 = MemberFactory.defaultMember().build();
        Member member5 = MemberFactory.defaultMember().build();
        Member member6 = MemberFactory.defaultMember().build();
        Member member7 = MemberFactory.defaultMember().build();
        Member member8 = MemberFactory.defaultMember().build();
        Member member9 = MemberFactory.defaultMember().build();
        Member member10 = MemberFactory.defaultMember().build();
        Member member11 = MemberFactory.defaultMember().build();
        Member member12 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 5", crew1, member1);
        Comment comment2 = Comment.of("댓글 4", crew1, member2);
        Comment comment3 = Comment.of("댓글 3", crew1, member3);
        Comment comment4 = Comment.of("댓글 2", crew1, member4);
        Comment comment5 = Comment.of("댓글 1", crew1, member5);
        memberRepository.saveAll(List.of(member1, member2, member3, member4, member5, member6, member7, member8, member9, member10, member11, member12));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member6);
        Comment reply2 = Comment.forReply(comment1, "대댓글 2", crew1, member7);
        Comment reply3 = Comment.forReply(comment1, "대댓글 3", crew1, member8);
        Comment reply4 = Comment.forReply(comment1, "대댓글 4", crew1, member9);
        Comment reply5 = Comment.forReply(comment2, "대댓글 5", crew1, member10);
        Comment reply6 = Comment.forReply(comment2, "대댓글 6", crew1, member11);
        Comment reply7 = Comment.forReply(comment2, "대댓글 7", crew1, member12);
        Comment reply8 = Comment.forReply(comment2, "대댓글 8", crew1, member6);
        Comment reply9 = Comment.forReply(comment3, "대댓글 9", crew1, member7);
        Comment reply10 = Comment.forReply(comment3, "대댓글 10", crew1, member8);
        Comment reply11 = Comment.forReply(comment3, "대댓글 11", crew1, member9);
        Comment reply12 = Comment.forReply(comment3, "대댓글 12", crew1, member10);
        Comment reply13 = Comment.forReply(comment4, "대댓글 13", crew1, member11);
        Comment reply14 = Comment.forReply(comment4, "대댓글 14", crew1, member12);
        Comment reply15 = Comment.forReply(comment4, "대댓글 15", crew1, member6);
        Comment reply16 = Comment.forReply(comment4, "대댓글 16", crew1, member7);
        Comment reply17 = Comment.forReply(comment5, "대댓글 17", crew1, member8);
        Comment reply18 = Comment.forReply(comment5, "대댓글 18", crew1, member9);
        Comment reply19 = Comment.forReply(comment5, "대댓글 19", crew1, member10);
        Comment reply20 = Comment.forReply(comment5, "대댓글 20", crew1, member11);
        Comment reply21 = Comment.forReply(comment5, "대댓글 21", crew1, member12);

        commentRepository.saveAll(
                List.of(
                        reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                        reply11, reply12, reply13, reply14, reply15, reply16, reply17, reply18, reply19,
                        reply20, reply21
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<Tuple> rn = jpaQueryFactory
                .select(comment,
                        SQLExpressions.rowNumber()
                                .over()
                                .partitionBy(comment.parent.id)
                                .as("rn")
                )
                .from(comment)
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.crew, crew).fetchJoin()
                .where(
                        comment.parent.id.in(1, 2, 3)
                )
                .fetch();
    }

    @Test
    @DisplayName("JPQLNextExpressions 테스트 3")
    public void jPQLNextExpressions3() {
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Member member3 = MemberFactory.defaultMember().build();
        Member member4 = MemberFactory.defaultMember().build();
        Member member5 = MemberFactory.defaultMember().build();
        Member member6 = MemberFactory.defaultMember().build();
        Member member7 = MemberFactory.defaultMember().build();
        Member member8 = MemberFactory.defaultMember().build();
        Member member9 = MemberFactory.defaultMember().build();
        Member member10 = MemberFactory.defaultMember().build();
        Member member11 = MemberFactory.defaultMember().build();
        Member member12 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member2).build();
        Comment comment1 = Comment.of("댓글 5", crew1, member1);
        Comment comment2 = Comment.of("댓글 4", crew1, member2);
        Comment comment3 = Comment.of("댓글 3", crew1, member3);
        Comment comment4 = Comment.of("댓글 2", crew1, member4);
        Comment comment5 = Comment.of("댓글 1", crew1, member5);
        memberRepository.saveAll(List.of(member1, member2, member3, member4, member5, member6, member7, member8, member9, member10, member11, member12));
        crewRepository.saveAll(List.of(crew1, crew2));
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));
        Comment reply1 = Comment.forReply(comment1, "대댓글 1", crew1, member6);
        Comment reply2 = Comment.forReply(comment1, "대댓글 2", crew1, member7);
        Comment reply3 = Comment.forReply(comment1, "대댓글 3", crew1, member8);
        Comment reply4 = Comment.forReply(comment1, "대댓글 4", crew1, member9);
        Comment reply5 = Comment.forReply(comment2, "대댓글 5", crew1, member10);
        Comment reply6 = Comment.forReply(comment2, "대댓글 6", crew1, member11);
        Comment reply7 = Comment.forReply(comment2, "대댓글 7", crew1, member12);
        Comment reply8 = Comment.forReply(comment2, "대댓글 8", crew1, member6);
        Comment reply9 = Comment.forReply(comment3, "대댓글 9", crew1, member7);
        Comment reply10 = Comment.forReply(comment3, "대댓글 10", crew1, member8);
        Comment reply11 = Comment.forReply(comment3, "대댓글 11", crew1, member9);
        Comment reply12 = Comment.forReply(comment3, "대댓글 12", crew1, member10);
        Comment reply13 = Comment.forReply(comment4, "대댓글 13", crew1, member11);
        Comment reply14 = Comment.forReply(comment4, "대댓글 14", crew1, member12);
        Comment reply15 = Comment.forReply(comment4, "대댓글 15", crew1, member6);
        Comment reply16 = Comment.forReply(comment4, "대댓글 16", crew1, member7);
        Comment reply17 = Comment.forReply(comment5, "대댓글 17", crew1, member8);
        Comment reply18 = Comment.forReply(comment5, "대댓글 18", crew1, member9);
        Comment reply19 = Comment.forReply(comment5, "대댓글 19", crew1, member10);
        Comment reply20 = Comment.forReply(comment5, "대댓글 20", crew1, member11);
        Comment reply21 = Comment.forReply(comment5, "대댓글 21", crew1, member12);

        commentRepository.saveAll(
                List.of(
                        reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                        reply11, reply12, reply13, reply14, reply15, reply16, reply17, reply18, reply19,
                        reply20, reply21
                )
        );

        entityManager.flush();
        entityManager.clear();


        List<Comment> root1 = blazeQueryFactory
                .select(comment)
                .from(JPAExpressions.select(comment)
                        .from(comment)
                        .where(comment.content.eq("root1")), comment)
                .fetch();

        QComment sub = new QComment("sub");
        NamedWindow blep = new NamedWindow("whihi").partitionBy(comment.parent.id);
        BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Tuple>(entityManager, cbf).from(comment)
                .window(blep)
                .select(comment.content.as("blep"), JPQLNextExpressions.rowNumber().over(blep), JPQLNextExpressions.lastValue(comment.content).over(blep))
                .where(comment.id.in(select(sub.id).from(sub)));
        List<Tuple> fetch = query.fetch();
    }
}
