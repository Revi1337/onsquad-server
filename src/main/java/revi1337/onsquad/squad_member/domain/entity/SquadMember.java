package revi1337.onsquad.squad_member.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static revi1337.onsquad.squad_member.domain.entity.vo.JoinStatus.PENDING;
import static revi1337.onsquad.squad_member.domain.entity.vo.SquadRole.GENERAL;
import static revi1337.onsquad.squad_member.domain.entity.vo.SquadRole.LEADER;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import revi1337.onsquad.common.domain.RequestEntity;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.entity.vo.JoinStatus;
import revi1337.onsquad.squad_member.domain.entity.vo.SquadRole;

@DynamicInsert
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_squadmember_squad_member", columnNames = {"squad_id", "member_id"})})
@AttributeOverrides({@AttributeOverride(name = "requestAt", column = @Column(name = "participate_at", nullable = false))})
public class SquadMember extends RequestEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "squad_id", nullable = false)
    private Squad squad;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ColumnDefault("'GENERAL'")
    @Enumerated(STRING)
    @Column(nullable = false)
    private SquadRole role;

    @ColumnDefault("'PENDING'")
    @Enumerated(STRING)
    @Column(nullable = false)
    private JoinStatus status;

    SquadMember(SquadRole role, JoinStatus status, LocalDateTime participantAt) {
        super(participantAt);
        this.role = role == null ? GENERAL : role;
        this.status = status == null ? PENDING : status;
    }

    void addOwner(Member member) {
        this.member = member;
    }

    public void addSquad(Squad squad) {
        this.squad = squad;
    }

    /**
     * 탈퇴 전 SquadMember 조회 시 Squad 정보를 같이 fetch 하지 않을 경우 Squad 조회 쿼리가 추가적으로 나갈 수 있다.
     * <p>
     * 정상적으로 탈퇴하면 Squad 의 잔류 인원이 1 증가한다. 이때 실제로 Squad 의 members 리스트에는 변화가 없지만, 이는 의도된 설계다.
     * <p>
     * 도메인 흐름 상, 탈퇴 시 고아객체 제거 및 Cascade 로 Squad 의 members 컬렉션에서 요소를 제거하는 것이 자연스럽지만, 모든 SquadMember 를 불러오는 것은 비효율적이므로 fetch 를 유도하지 않는다.
     */
    public void leaveSquad() {
        releaseOwner();
        squad.increaseRemain();
        releaseSquad();
    }

    private void releaseOwner() {
        this.member = null;
    }

    private void releaseSquad() {
        this.squad = null;
    }

    public boolean isNotLeader() {
        return !isLeader();
    }

    public boolean isLeader() {
        return this.role == LEADER;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SquadMember that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public boolean matchMemberId(Long memberId) {
        return member.getId().equals(memberId);
    }
}
