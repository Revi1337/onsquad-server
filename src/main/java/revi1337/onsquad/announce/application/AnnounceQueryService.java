package revi1337.onsquad.announce.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithFixAndModifyStateResponse;
import revi1337.onsquad.announce.application.dto.response.AnnouncesWithWriteStateResponse;
import revi1337.onsquad.announce.domain.AnnouncePolicy;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@Service
@RequiredArgsConstructor
public class AnnounceQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final AnnounceCacheService announceCacheService;
    private final AnnounceRepository announceRepository;

    public AnnounceWithFixAndModifyStateResponse findAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        AnnounceResult announce = announceCacheService.getAnnounce(crewId, announceId);

        boolean canFix = AnnouncePolicy.canFixable(me);
        boolean canModify = AnnouncePolicy.canModify(me, announce.writer().id());

        return AnnounceWithFixAndModifyStateResponse.from(canFix, canModify, announce);
    }

    @Transactional(readOnly = true)
    public AnnouncesWithWriteStateResponse findAnnounces(Long memberId, Long crewId) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        List<AnnounceResult> announces = announceRepository.fetchAllByCrewId(crewId);

        boolean canWrite = AnnouncePolicy.canWrite(me);

        return AnnouncesWithWriteStateResponse.from(canWrite, announces);
    }
}
