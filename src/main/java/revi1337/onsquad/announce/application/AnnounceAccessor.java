package revi1337.onsquad.announce.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.announce.error.AnnounceBusinessException;
import revi1337.onsquad.announce.error.AnnounceErrorCode;

@Component
@RequiredArgsConstructor
public class AnnounceAccessor {

    private final AnnounceRepository announceRepository;

    public Announce getById(Long announceId) {
        return announceRepository.findById(announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }
}
