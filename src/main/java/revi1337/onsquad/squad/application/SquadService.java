package revi1337.onsquad.squad.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.category.util.CategoryTypeUtil;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoWithOwnerFlagDto;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.application.dto.SquadInfoDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad_member.domain.SquadMember;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadService {

    private final SquadRepository squadRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Transactional
    public void createNewSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        memberRepository.getById(memberId);
        Crew crew = crewRepository.getById(crewId);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        persistSquad(dto, crewMember, crew);
    }

    public SquadInfoDto findSquad(Long memberId, Long crewId, Long squadId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        return SquadInfoDto.from(squadRepository.getSquadById(squadId));
    }

    public List<SquadInfoDto> findSquads(Long crewId, CategoryCondition condition, Pageable pageable) {
        return squadRepository.findSquadsByCrewId(crewId, condition.categoryType(), pageable).stream()
                .map(SquadInfoDto::from)
                .collect(Collectors.toList());
    }

    public List<SimpleSquadInfoWithOwnerFlagDto> fetchSquadsWithOwnerFlag(
            Long memberId,
            Long crewId,
            Pageable pageable
    ) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return squadRepository.fetchSquadsWithOwnerFlag(memberId, crewId, pageable).stream()
                .map(SimpleSquadInfoWithOwnerFlagDto::from)
                .toList();
    }

    private void persistSquad(SquadCreateDto dto, CrewMember crewMember, Crew crew) {
        Squad persistedSquad = insertSquadAndRegisterAdmin(dto, crewMember, crew);
        batchInsertSquadCategories(dto, persistedSquad);
    }

    private Squad insertSquadAndRegisterAdmin(SquadCreateDto dto, CrewMember crewMember, Crew crew) {
        Squad squad = dto.toEntity(crewMember, crew);
        squad.addSquadMember(SquadMember.forLeader(crewMember, LocalDateTime.now()));
        return squadRepository.save(squad);
    }

    private void batchInsertSquadCategories(SquadCreateDto dto, Squad persistedSquad) {
        List<CategoryType> categoryTypes = CategoryType.fromTexts(dto.categories());
        List<CategoryType> possibleCategoryTypes = CategoryTypeUtil.extractPossible(categoryTypes);
        List<Category> categories = Category.fromCategoryTypes(possibleCategoryTypes);
        squadRepository.batchInsertSquadCategories(persistedSquad.getId(), categories);
    }
}
