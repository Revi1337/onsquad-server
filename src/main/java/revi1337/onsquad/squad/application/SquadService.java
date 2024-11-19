package revi1337.onsquad.squad.application;

import static revi1337.onsquad.squad.error.SquadErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.squad.error.SquadErrorCode.NOTMATCH_CREWINFO;
import static revi1337.onsquad.squad.error.SquadErrorCode.OWNER_CANT_PARTICIPANT;

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
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.application.dto.SquadInfoDto;
import revi1337.onsquad.squad.application.dto.SquadJoinDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_participant.domain.SquadParticipantRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadService {

    private final SquadRepository squadRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadParticipantRepository squadParticipantRepository;

    public SquadInfoDto findSquad(Long squadId) {
        return SquadInfoDto.from(squadRepository.getSquadById(squadId));
    }

    public List<SquadInfoDto> findSquads(Long crewId, CategoryCondition condition, Pageable pageable) {
        return squadRepository.findSquadsByCrewId(crewId, condition.categoryType(), pageable).stream()
                .map(SquadInfoDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createNewSquad(Long memberId, Long crewId, SquadCreateDto dto) {
        memberRepository.getById(memberId);
        Crew crew = crewRepository.getById(crewId);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        persistSquad(dto, crewMember, crew);
    }

    @Transactional
    public void submitParticipationRequest(Long memberId, Long crewId, SquadJoinDto dto) {
        Squad squad = squadRepository.getByIdWithOwnerAndCrewAndSquadMembers(dto.squadId());
        checkCrewInfoIsMatch(crewId, squad);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(squad.getCrew().getId(), memberId);
        validateSquadMetaData(squad, crewMember);
        squadParticipantRepository.upsertSquadParticipant(squad.getId(), crewMember.getId(), LocalDateTime.now());
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
        List<CategoryType> categoryTypes = CategoryTypeUtil.extractPossible(CategoryType.fromTexts(dto.categories()));
        List<Category> categories = Category.fromCategoryTypes(categoryTypes);
        squadRepository.batchInsertSquadCategories(persistedSquad.getId(), categories);
    }

    private void checkCrewInfoIsMatch(Long requestCrewId, Squad squad) {
        if (!squad.getCrew().getId().equals(requestCrewId)) {
            throw new SquadBusinessException.NotMatchCrewInfo(NOTMATCH_CREWINFO);
        }
    }

    private void validateSquadMetaData(Squad squad, CrewMember crewMember) {
        checkDifferenceSquadCreator(squad, crewMember);
        checkAlreadyJoined(squad, crewMember);
    }

    private void checkDifferenceSquadCreator(Squad squad, CrewMember crewMember) {
        if (squad.getCrewMember().equals(crewMember)) {
            throw new SquadBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
        }
    }

    private void checkAlreadyJoined(Squad squad, CrewMember crewMember) {
        for (SquadMember squadMember : squad.getSquadMembers()) {
            if (squadMember.getCrewMember().getId().equals(crewMember.getId())) {
                throw new SquadBusinessException.AlreadyParticipant(ALREADY_JOIN, squad.getTitle().getValue());
            }
        }
    }
}
