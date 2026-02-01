package revi1337.onsquad.crew_hashtag.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class CrewHashtag {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    public CrewHashtag(Crew crew, Hashtag hashtag) {
        this.crew = crew;
        this.hashtag = hashtag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrewHashtag that)) {
            return false;
        }
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
