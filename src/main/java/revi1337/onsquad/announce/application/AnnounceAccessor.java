package revi1337.onsquad.announce.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.error.AnnounceBusinessException;
import revi1337.onsquad.announce.domain.error.AnnounceErrorCode;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;

@Component
@RequiredArgsConstructor
public class AnnounceAccessor {

    private final AnnounceRepository announceRepository;

    public Announce getById(Long announceId) {
        return announceRepository.findById(announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }
}
