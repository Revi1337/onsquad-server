package revi1337.onsquad.squad_comment.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static revi1337.onsquad.squad_comment.domain.error.SquadCommentErrorCode.INVALID_LENGTH;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_comment.domain.error.SquadCommentDomainException;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class SquadComment extends BaseEntity {

    private static final int MAX_LENGTH = 250;
    private static final int PERSIST_MAX_LENGTH = MAX_LENGTH * 3;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = PERSIST_MAX_LENGTH)
    private String content;

    private boolean deleted;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "squad_id", nullable = false)
    private Squad squad;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private SquadComment parent;

    @OneToMany(mappedBy = "parent")
    private final List<SquadComment> replies = new ArrayList<>();

    private SquadComment(String content, Squad squad, Member member) {
        this(content, squad, member, null);
    }

    private SquadComment(String content, Squad squad, Member member, SquadComment parent) {
        validate(content);
        this.content = content;
        this.squad = squad;
        this.member = member;
        this.parent = parent;
    }

    public static SquadComment create(String content, Squad squad, Member member) {
        return new SquadComment(content, squad, member);
    }

    public static SquadComment createReply(SquadComment parent, String content, Squad squad, Member member) {
        return new SquadComment(content, squad, member, parent);

    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        if (!deleted) {
            this.deleted = true;
            this.deletedAt = LocalDateTime.now();
        }
    }

    public boolean isNotParent() {
        return !isParent();
    }

    public boolean isParent() {
        return parent == null;
    }

    private void validate(String content) {
        if (content == null) {
            throw new NullPointerException("댓글은 null 일 수 없습니다.");
        }

        if (content.isEmpty() || content.length() > MAX_LENGTH) {
            throw new SquadCommentDomainException.InvalidLength(INVALID_LENGTH, MAX_LENGTH);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SquadComment comment)) {
            return false;
        }
        return id != null && Objects.equals(getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
