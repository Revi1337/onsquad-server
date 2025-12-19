package revi1337.onsquad.announce.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithFixAndModifyStateResponse;
import revi1337.onsquad.announce.application.dto.response.AnnouncesWithWriteStateResponse;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AnnounceQueryService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final AnnounceRepository announceRepository;

    public AnnounceWithFixAndModifyStateResponse findAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        AnnounceResult domainDto = announceRepository.fetchCacheByCrewIdAndId(crewId, announceId);
        boolean canFix = crewMember.isOwner();
        boolean canModify = domainDto.writer().id().equals(memberId);

        return AnnounceWithFixAndModifyStateResponse.from(canFix, canModify, domainDto);
    }

    public AnnouncesWithWriteStateResponse findAnnounces(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        List<AnnounceResult> domainDtos = announceRepository.fetchAllByCrewId(crewId, pageable).getContent();

        return AnnouncesWithWriteStateResponse.from(crewMember.isGreaterThenManager(), domainDtos);
    }
}
