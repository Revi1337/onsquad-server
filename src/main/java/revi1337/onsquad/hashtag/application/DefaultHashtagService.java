package revi1337.onsquad.hashtag.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.hashtag.domain.repository.HashtagRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DefaultHashtagService implements HashtagService {

    private final HashtagRepository hashtagRepository;

    @Override
    public List<String> findHashtags() {
        return hashtagRepository.findAll().stream()
                .map(hashtag -> hashtag.getHashtagType().getText())
                .toList();
    }
}
