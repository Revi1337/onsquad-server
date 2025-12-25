package revi1337.onsquad.hashtag.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;

public interface HashtagJpaRepository extends JpaRepository<Hashtag, Long> {

}
