package revi1337.onsquad.hashtag.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.hashtag.domain.repository.HashtagJpaRepository;

@Service
@Transactional(readOnly = true)
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
