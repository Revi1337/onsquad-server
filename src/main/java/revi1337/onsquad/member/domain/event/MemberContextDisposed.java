package revi1337.onsquad.member.domain.event;

import java.util.List;
import revi1337.onsquad.announce.domain.result.AnnounceReference;

public record MemberContextDisposed(
        Long memberId,
        String memberImageUrl,
        List<AnnounceReference> announceReferences
) {

}
