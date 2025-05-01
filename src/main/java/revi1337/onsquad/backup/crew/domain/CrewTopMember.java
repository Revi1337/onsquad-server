package revi1337.onsquad.backup.crew.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewTopMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private int counter;

    @Column(nullable = false)
    private int ranks;

    public CrewTopMember(Long crewId, int ranks, int counter, Long memberId, String nickname, String mbti,
                         LocalDateTime participateAt) {
        this.crewId = crewId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.mbti = mbti;
        this.participateAt = participateAt;
        this.counter = counter;
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
