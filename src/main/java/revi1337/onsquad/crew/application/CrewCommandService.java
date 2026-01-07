package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.domain.CrewPolicy;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.member.application.MemberAccessor;
import revi1337.onsquad.member.domain.entity.Member;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewCommandService {

    private final MemberAccessor memberAccessor;
    private final CrewAccessor crewAccessor;
    private final CrewRepository crewRepository;
    private final CrewHashtagRepository crewHashtagRepository;
    private final CrewContextHandler crewContextHandler;

    public Long newCrew(Long memberId, CrewCreateDto dto, String newImageUrl) {
        Member member = memberAccessor.getById(memberId);
        crewAccessor.validateCrewNameIsDuplicate(dto.name());
        Crew crew = crewRepository.save(Crew.create(member, dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink(), newImageUrl));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
        return crew.getId();
    }

    public void updateCrew(Long memberId, Long crewId, CrewUpdateDto dto) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureCrewUpdatable(crew, memberId);
        crew.update(dto.name(), dto.introduce(), dto.detail(), dto.kakaoLink());
        crewHashtagRepository.deleteByCrewIdIn(List.of(crew.getId()));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(dto.hashtags()));
    }

    public void deleteCrew(Long memberId, Long crewId) {
        Crew crew = crewAccessor.getById(crewId);
        CrewPolicy.ensureCrewDeletable(crew, memberId);
        crewContextHandler.disposeContext(crew);
    }
}
