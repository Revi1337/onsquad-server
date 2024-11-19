package revi1337.onsquad.backup.crew.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewTopCache {

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

    @Builder
    private CrewTopCache(
            Long id,
            Long crewId,
            Long memberId,
            String nickname,
            String mbti,
            LocalDateTime participateAt,
            int counter,
            int ranks
    ) {
        this.id = id;
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
        if (!(o instanceof CrewTopCache that)) {
            return false;
        }
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public static CrewTopCache from(Top5CrewMemberDomainDto dto) {
        return CrewTopCache.builder()
                .crewId(dto.crewId())
                .memberId(dto.memberId())
                .nickname(dto.nickname())
                .mbti(dto.mbti())
                .participateAt(dto.participateAt())
                .counter(dto.counter())
                .ranks(dto.rank())
                .build();
    }
}
