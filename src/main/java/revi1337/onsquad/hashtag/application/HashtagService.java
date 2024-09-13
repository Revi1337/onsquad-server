package revi1337.onsquad.hashtag.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.hashtag.domain.HashtagRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public List<String> findHashtags() {
        return hashtagRepository.findAllHashtags().stream()
                .map(hashtag -> hashtag.getHashtagType().getText())
                .toList();
    }
}
