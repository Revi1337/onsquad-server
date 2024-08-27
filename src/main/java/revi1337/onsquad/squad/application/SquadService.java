package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.domain.category.CategoryRepository;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.participant.domain.SquadParticipant;
import revi1337.onsquad.participant.domain.SquadParticipantRepository;
import revi1337.onsquad.squad.domain.*;
import revi1337.onsquad.squad.domain.category.Category;
import revi1337.onsquad.squad.domain.vo.category.CategoryType;
import revi1337.onsquad.squad.dto.SquadCreateDto;
import revi1337.onsquad.squad.dto.SquadJoinDto;
import revi1337.onsquad.squad.dto.SquadDto;
import revi1337.onsquad.squad.dto.request.CategoryCondition;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad.util.category.CategoryTypeUtil;
import revi1337.onsquad.squad_member.domain.SquadMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.*;
import static revi1337.onsquad.squad.error.SquadErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadService {

    private final SquadRepository squadRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final SquadParticipantRepository squadParticipantRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public SquadDto findSquad(Long id) {
        Squad squad = squadRepository.getSquadByIdAndTitleWithMember(id);
        return SquadDto.from(squad);
    }

    @Transactional(readOnly = true)
    public List<SquadDto> findSquads(String crewName, CategoryCondition condition, Pageable pageable) {
        return squadRepository.findSquadsByCrewName(new Name(crewName), condition.categoryType(), pageable).stream()
                .map(SquadDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createNewSquad(SquadCreateDto dto, Long memberId) {
        Member member = getMemberById(memberId);
        Crew crew = getCrewWithMembersByName(dto.crewName());
        persistSquadIfCrewMemberIsValid(dto, member, crew);
    }

    @Transactional
    public void submitParticipationRequest(SquadJoinDto dto, Long memberId) {
        Member member = getMemberById(memberId);
        Crew crew = getCrewWithMembersByName(dto.crewName());
        CrewMember crewMember = checkMemberInCrew(dto.crewName(), crew, member);
        Squad squad = squadRepository.getSquadByIdWithSquadMembers(dto.squadId());
        validateSquadMetadata(dto, squad, crewMember, member);
        addParticipantRequest(squad, crewMember);
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOTFOUND, memberId));
    }

    private Crew getCrewWithMembersByName(String crewName) {
        return crewRepository.findByNameWithCrewMembers(new Name(crewName))
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(CrewErrorCode.NOTFOUND_CREW, crewName));
    }

    private void persistSquadIfCrewMemberIsValid(SquadCreateDto dto, Member member, Crew crew) {
        crew.getCrewMembers().stream()
                .filter(crewMember -> crewMember.getMember().equals(member))
                .findFirst()
                .ifPresentOrElse(
                        crewMember -> persistSquadAndRegisterAdmin(dto, crewMember, crew),
                        () -> { throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT, dto.crewName()); }
                );
    }

    private void persistSquadAndRegisterAdmin(SquadCreateDto dto, CrewMember crewMember, Crew crew) {
        Squad squad = dto.toEntity(crewMember, crew);
        squad.addSquadMember(SquadMember.forLeader(crewMember));
        Squad persistedSquad = squadRepository.save(squad);
        List<CategoryType> categoryTypes = CategoryTypeUtil.extractPossible(CategoryType.fromTexts(dto.categories()));
        List<Category> categories = categoryRepository.findCategoriesInSecondCache(categoryTypes);
        squadRepository.batchInsertSquadCategories(persistedSquad.getId(), categories);
    }

    private CrewMember checkMemberInCrew(String crewName, Crew crew, Member member) {
        return crew.getCrewMembers().stream()
                .filter(crewMember -> crewMember.getMember().equals(member))
                .findFirst()
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT, crewName));
    }

    private void validateSquadMetadata(SquadJoinDto dto, Squad squad, CrewMember crewMember, Member member) {
        checkDifferenceSquadCreator(squad, crewMember);
        checkSquadInCrew(dto, squad);
        checkSquadMemberAlreadyJoined(squad, member);
    }

    private void checkDifferenceSquadCreator(Squad squad, CrewMember crewMember) {
        if (squad.getCrewMember().equals(crewMember)) {
            throw new SquadBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
        }
    }

    private void checkSquadInCrew(SquadJoinDto dto, Squad squad) {
        if (!squad.getCrew().getName().equals(new Name(dto.crewName()))) {
            throw new SquadBusinessException.NotInCrew(NOT_IN_CREW, dto.crewName());
        }
    }

    private void checkSquadMemberAlreadyJoined(Squad squad, Member member) {
        for (SquadMember squadMember : squad.getSquadMembers()) {
            if (squadMember.getCrewMember().getId().equals(member.getId())) {
                throw new SquadBusinessException.AlreadyParticipant(ALREADY_JOIN, squad.getTitle().getValue());
            }
        }
    }

    private void addParticipantRequest(Squad squad, CrewMember crewMember) {
        synchronized (this) {
            squadParticipantRepository.findBySquadIdAndCrewMemberId(squad.getId(), crewMember.getId()).ifPresentOrElse(
                    squadParticipant -> {
                        squadParticipant.updateRequestTimestamp(LocalDateTime.now());
                        squadParticipantRepository.saveAndFlush(squadParticipant);
                    },
                    () -> squadParticipantRepository.save(SquadParticipant.of(squad, crewMember, LocalDateTime.now()))
            );
        }
    }
}
