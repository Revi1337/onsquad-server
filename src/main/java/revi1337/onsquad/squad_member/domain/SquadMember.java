package revi1337.onsquad.squad_member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad_member.domain.vo.SquadRole;

import java.util.Objects;

@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "request_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "participate_at"))
})
public class SquadMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id", nullable = false)
    private Squad squad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ColumnDefault("'GENERAL'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SquadRole role;

    @Builder
    private SquadMember(Long id, Squad squad, Member member, SquadRole role) {
        this.id = id;
        this.squad = squad;
        this.member = member;
        this.role = role;
    }

    public static SquadMember forAdmin(Member member) {
        return SquadMember.builder()
                .member(member)
                .role(SquadRole.ADMIN)
                .build();
    }

    public void addSquad(Squad squad) {
        this.squad = squad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SquadMember that)) return false;
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
