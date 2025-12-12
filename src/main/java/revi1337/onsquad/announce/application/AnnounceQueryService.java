package revi1337.onsquad.announce.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.AnnounceWithFixAndModifyStateDto;
import revi1337.onsquad.announce.application.dto.AnnouncesWithWriteStateDto;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AnnounceQueryService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final AnnounceRepository announceRepository;

    public AnnounceWithFixAndModifyStateDto findAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        AnnounceDomainDto domainDto = announceRepository.fetchCacheByCrewIdAndId(crewId, announceId);
        boolean canFix = crewMember.isOwner();
        boolean canModify = domainDto.writer().id().equals(memberId);

        return AnnounceWithFixAndModifyStateDto.from(canFix, canModify, domainDto);
    }

    public AnnouncesWithWriteStateDto findAnnounces(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        List<AnnounceDomainDto> domainDtos = announceRepository.fetchAllByCrewId(crewId, pageable).getContent();

        return AnnouncesWithWriteStateDto.from(crewMember.isGreaterThenManager(), domainDtos);
    }
}
