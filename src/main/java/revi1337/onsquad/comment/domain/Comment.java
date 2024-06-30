package revi1337.onsquad.comment.domain;

import jakarta.persistence.*;
import lombok.*;
import revi1337.onsquad.comment.error.exception.CommentDomainException;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static revi1337.onsquad.comment.error.CommentErrorCode.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    private static final int MAX_LENGTH = 250;
    private static final int PERSIST_MAX_LENGTH = MAX_LENGTH * 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = PERSIST_MAX_LENGTH)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private final List<Comment> replies = new ArrayList<>();

    public Comment(String content) {
        validate(content);
        this.content = content;
    }

    private void validate(String content) {
        if (content == null) {
            throw new NullPointerException("댓글은 null 일 수 없습니다.");
        }

        if (content.isEmpty() || content.length() > MAX_LENGTH) {
            throw new CommentDomainException.InvalidLength(INVALID_LENGTH, MAX_LENGTH);
        }
    }

    @Builder
    private Comment(Long id, String content, Crew crew, Member member, Comment parent) {
        this.id = id;
        this.content = content;
        this.crew = crew;
        this.member = member;
        this.parent = parent;
    }

    public static Comment of(String content, Crew crew, Member member) {
        return Comment.builder()
                .content(content)
                .crew(crew)
                .member(member)
                .build();
    }

    public static Comment forReply(Comment parent, String content, Crew crew, Member member) {
        return Comment.builder()
                .parent(parent)
                .content(content)
                .crew(crew)
                .member(member)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return id != null && Objects.equals(getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
