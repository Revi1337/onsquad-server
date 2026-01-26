package revi1337.onsquad.hashtag.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.hashtag.domain.repository.HashtagJpaRepository;

@Service
@RequiredArgsConstructor
public class DefaultHashtagService implements HashtagService {

    private final HashtagJpaRepository hashtagJpaRepository;

    @Override
    public List<String> findHashtags() {
        return hashtagJpaRepository.findAll().stream()
                .map(hashtag -> hashtag.getHashtagType().getText())
                .toList();
    }
}
