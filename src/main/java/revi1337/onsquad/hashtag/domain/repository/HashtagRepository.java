package revi1337.onsquad.hashtag.domain.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Deprecated
    default List<Hashtag> findAllHashtags() {
        return HashtagType.unmodifiableList().stream()
                .map(hashtagType -> findById(hashtagType.getPk()).get())
                .collect(Collectors.toList());
    }

    @Deprecated
    default List<Hashtag> findHashtagsInSecondCache(List<HashtagType> hashtagTypes) {
        return hashtagTypes.stream()
                .map(hashtagType -> findById(hashtagType.getPk()).get())
                .collect(Collectors.toList());
    }
}
