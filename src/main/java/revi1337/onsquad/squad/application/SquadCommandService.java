package revi1337.onsquad.squad.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad_member.domain.SquadMember;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadCommandService {

    private final SquadRepository squadRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public void createNewSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        memberRepository.getById(memberId);
        Crew crew = crewRepository.getById(crewId);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        persistSquad(dto, crewMember, crew);
    }

    private void persistSquad(SquadCreateDto dto, CrewMember crewMember, Crew crew) {
        Squad squad = dto.toEntity(crewMember, crew);
        squad.addSquadMember(SquadMember.forLeader(crewMember, LocalDateTime.now()));
        Squad persistSquad = squadRepository.save(squad);

        List<Category> categories = Category.fromCategoryTypes(dto.categories());
        squadRepository.batchInsertSquadCategories(persistSquad.getId(), categories);
    }
}
