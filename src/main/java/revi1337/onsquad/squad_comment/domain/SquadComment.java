package revi1337.onsquad.squad_comment.domain;

import static revi1337.onsquad.squad_comment.error.SquadCommentErrorCode.INVALID_LENGTH;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentDomainException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SquadComment extends BaseEntity {

    private static final int MAX_LENGTH = 250;
    private static final int PERSIST_MAX_LENGTH = MAX_LENGTH * 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = PERSIST_MAX_LENGTH)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id")
    private Squad squad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_member_id")
    private CrewMember crewMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private SquadComment parent;

    @OneToMany(mappedBy = "parent")
    private final List<SquadComment> replies = new ArrayList<>();

    @Builder
    private SquadComment(Long id, String content, Squad squad, CrewMember crewMember, SquadComment parent) {
        validate(content);
        this.id = id;
        this.content = content;
        this.squad = squad;
        this.crewMember = crewMember;
        this.parent = parent;
    }

    public static SquadComment create(String content, Squad squad, CrewMember crewMember) {
        return SquadComment.builder()
                .content(content)
                .squad(squad)
                .crewMember(crewMember)
                .build();
    }

    public static SquadComment createReply(SquadComment parent, String content, Squad squad, CrewMember crewMember) {
        return SquadComment.builder()
                .parent(parent)
                .content(content)
                .squad(squad)
                .crewMember(crewMember)
                .build();
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

    public boolean hasNotSameSquadId(Long squadId) {
        return !hasSameSquadId(squadId);
    }

    public boolean hasSameSquadId(Long squadId) {
        return squadId.equals(squad.getId());
    }

    private void validate(String content) {
        if (content == null) {
            throw new NullPointerException("댓글은 null 일 수 없습니다.");
        }

        if (content.isEmpty() || content.length() > MAX_LENGTH) {
            throw new SquadCommentDomainException.InvalidLength(INVALID_LENGTH, MAX_LENGTH);
        }
    }
}
