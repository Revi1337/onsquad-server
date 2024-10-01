package revi1337.onsquad.hashtag.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.hashtag.domain.HashtagRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
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
