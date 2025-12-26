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

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AnnounceQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final AnnounceCacheService announceCacheService;
    private final AnnounceRepository announceRepository;

    public AnnounceWithFixAndModifyStateResponse findAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        AnnounceResult announce = announceCacheService.getAnnounce(crewId, announceId);
        boolean canFix = AnnouncePolicy.canFixable(crewMember);
        boolean canModify = AnnouncePolicy.canModify(announce.writer().id(), memberId);

        return AnnounceWithFixAndModifyStateResponse.from(canFix, canModify, announce);
    }

    public AnnouncesWithWriteStateResponse findAnnounces(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        List<AnnounceResult> announces = announceRepository.fetchAllByCrewId(crewId);

        return AnnouncesWithWriteStateResponse.from(crewMember.isGreaterThenManager(), announces);
    }
}
