package revi1337.onsquad.announce.application;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithPinAndModifyStateResponse;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithRoleStateResponse;
import revi1337.onsquad.announce.application.dto.response.AnnouncesWithWriteStateResponse;
import revi1337.onsquad.announce.domain.AnnouncePolicy;
import revi1337.onsquad.announce.domain.model.AnnounceDetail;
import revi1337.onsquad.announce.domain.model.AnnounceDetails;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.vo.CrewRole;
import revi1337.onsquad.crew_member.domain.model.CrewMembers;

@Service
@RequiredArgsConstructor
public class AnnounceQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final AnnounceCacheService announceCacheService;
    private final AnnounceRepository announceRepository;

    public AnnounceWithPinAndModifyStateResponse findAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        AnnounceDetail announce = announceCacheService.getAnnounce(crewId, announceId);

        CrewRole role = me.getRole();
        boolean canPin = AnnouncePolicy.canPin(me);
        boolean canModify = AnnouncePolicy.canModify(me, announce.writer().id());

        return AnnounceWithPinAndModifyStateResponse.from(role, canPin, canModify, announce);
    }

    @Transactional(readOnly = true)
    public AnnouncesWithWriteStateResponse findAnnounces(Long memberId, Long crewId) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        AnnounceDetails announces = announceRepository.fetchAllByCrewId(crewId);
        CrewMembers crewMembers = crewMemberAccessor.findAllByCrewIdAndMemberIdIn(crewId, announces.getWriterIds());
        Map<Long, CrewRole> memberRoleMap = crewMembers.splitRolesByMemberId();

        boolean canWrite = AnnouncePolicy.canWrite(me);
        List<AnnounceWithRoleStateResponse> response = announces.values().stream()
                .map(detail -> AnnounceWithRoleStateResponse.from(memberRoleMap.get(detail.writer().id()), detail))
                .toList();

        return AnnouncesWithWriteStateResponse.from(canWrite, response);
    }
}
