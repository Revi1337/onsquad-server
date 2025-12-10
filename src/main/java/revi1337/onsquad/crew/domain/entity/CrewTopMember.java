package revi1337.onsquad.crew.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class CrewTopMember {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long crewId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String nickname;

    private String mbti;

    @Column(nullable = false)
    private LocalDateTime participateAt;

    @Column(nullable = false)
    private int contribute;

    @Column(nullable = false)
    private int ranks;

    public CrewTopMember(Long crewId, int ranks, int contribute, Long memberId, String nickname, String mbti, LocalDateTime participateAt) {
        this.crewId = crewId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.mbti = mbti;
        this.participateAt = participateAt;
        this.contribute = contribute;
        this.ranks = ranks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrewTopMember that)) {
            return false;
        }
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
