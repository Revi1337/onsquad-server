package revi1337.onsquad.crew_comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_comment.error.exception.CrewCommentDomainException;
import revi1337.onsquad.crew_member.domain.CrewMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static revi1337.onsquad.crew_comment.error.CrewCommentErrorCode.INVALID_LENGTH;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CrewComment extends BaseEntity {

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
    @JoinColumn(name = "crew_member_id")
    private CrewMember crewMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CrewComment parent;

    @OneToMany(mappedBy = "parent")
    private final List<CrewComment> replies = new ArrayList<>();

    public CrewComment(String content) {
        validate(content);
        this.content = content;
    }

    private void validate(String content) {
        if (content == null) {
            throw new NullPointerException("댓글은 null 일 수 없습니다.");
        }

        if (content.isEmpty() || content.length() > MAX_LENGTH) {
            throw new CrewCommentDomainException.InvalidLength(INVALID_LENGTH, MAX_LENGTH);
        }
    }

    @Builder
    private CrewComment(Long id, String content, Crew crew, CrewMember crewMember, CrewComment parent) {
        this.id = id;
        this.content = content;
        this.crew = crew;
        this.crewMember = crewMember;
        this.parent = parent;
    }

    public static CrewComment create(String content, Crew crew, CrewMember crewMember) {
        return CrewComment.builder()
                .content(content)
                .crew(crew)
                .crewMember(crewMember)
                .build();
    }

    public static CrewComment createReply(CrewComment parent, String content, Crew crew, CrewMember crewMember) {
        return CrewComment.builder()
                .parent(parent)
                .content(content)
                .crew(crew)
                .crewMember(crewMember)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrewComment comment)) return false;
        return id != null && Objects.equals(getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
