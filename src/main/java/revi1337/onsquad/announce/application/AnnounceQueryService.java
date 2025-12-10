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
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AnnounceQueryService {

    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;

    public AnnounceWithFixAndModifyStateDto findAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        AnnounceDomainDto domainDto = announceRepository.fetchCacheByCrewIdAndId(crewId, announceId);
        boolean canFix = crewMember.isOwner();
        boolean canModify = domainDto.writer().id().equals(memberId);

        return AnnounceWithFixAndModifyStateDto.from(canFix, canModify, domainDto);
    }

    public AnnouncesWithWriteStateDto findAnnounces(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        List<AnnounceDomainDto> domainDtos = announceRepository.fetchAllByCrewId(crewId, pageable).getContent();

        return AnnouncesWithWriteStateDto.from(crewMember.isGreaterThenManager(), domainDtos);
    }

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) {  // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }
}
