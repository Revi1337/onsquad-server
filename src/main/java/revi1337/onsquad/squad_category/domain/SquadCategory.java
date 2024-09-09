package revi1337.onsquad.squad_category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.category.domain.Category;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SquadCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id", nullable = false)
    private Squad squad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
