package revi1337.onsquad.hashtag.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Hashtag {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HashtagType hashtagType;

    private Hashtag(HashtagType hashtagType) {
        this.id = hashtagType.getPk();
        this.hashtagType = hashtagType;
    }

    public static List<Hashtag> fromHashtagTypes(List<HashtagType> hashtagTypes) {
        return hashtagTypes.stream()
                .map(Hashtag::fromHashtagType)
                .collect(Collectors.toList());
    }

    public static Hashtag fromHashtagType(HashtagType hashtagType) {
        return new Hashtag(hashtagType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hashtag hashtag)) return false;
        return id != null && Objects.equals(getId(), hashtag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
