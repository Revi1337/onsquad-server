package revi1337.onsquad.crew.domain;

import jakarta.persistence.*;
import lombok.*;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.image.domain.Image;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Crew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Introduce introduce;

    @Embedded
    private Detail detail;

    @Embedded
    private HashTags hashTags;

    private String kakaoLink;

    private String discordLink;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    @Builder
    private Crew(Long id, Name name, Introduce introduce, Detail detail, HashTags hashTags, Image image, String kakaoLink, String discordLink) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.detail = detail;
        this.hashTags = hashTags;
        this.image = image;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Crew crew)) return false;
        return id != null && Objects.equals(getId(), crew.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
