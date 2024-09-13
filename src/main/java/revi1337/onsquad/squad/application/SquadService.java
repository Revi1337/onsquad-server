package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad.application.dto.SquadInfoDto;
import revi1337.onsquad.squad_participant.domain.SquadParticipantRepository;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.squad.domain.*;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.application.dto.SquadJoinDto;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.category.util.CategoryTypeUtil;
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
    private final CrewMemberRepository crewMemberRepository;
    private final SquadParticipantRepository squadParticipantRepository;

    public SquadInfoDto findSquad(Long id) {
        return SquadInfoDto.from(squadRepository.getSquadById(id));
    }

    public List<SquadInfoDto> findSquads(String crewName, CategoryCondition condition, Pageable pageable) {
        return squadRepository.findSquadsByCrewName(new Name(crewName), condition.categoryType(), pageable).stream()
                .map(SquadInfoDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createNewSquad(SquadCreateDto dto, Long memberId) {
        Member member = getMemberById(memberId);
        Crew crew = crewRepository.getByNameWithCrewMembers(new Name(dto.crewName()));
        persistSquadIfCrewMemberIsValid(dto, member, crew);
    }

    /**
     * Squad 가 있는지 확인 (0)
     * Squad 가 속한 Crew 가 올바른지 확인 (0)
     * CrewMember 가 있는지 확인 (Squad 가 속한 Crew 에 Member 가 속해있는지 확인)
     * Squad 작성자와 CrewMember 가 다른지 확인 (0)
     * Squad 에 이미 CrewMember 가 있는지 확인 (0)
     */
    @Transactional
    public void submitParticipationRequest(SquadJoinDto dto, Long memberId) {
        Squad squad = squadRepository.getByIdWithOwnerAndCrewAndSquadMembers(dto.squadId());
        checkCrewInfoIsMatch(dto, squad);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(squad.getCrew().getId(), memberId);
        validateSquadMetaData(squad, crewMember);
        squadParticipantRepository.upsertSquadParticipant(squad.getId(), crewMember.getId(), LocalDateTime.now());
    }

    private void checkCrewInfoIsMatch(SquadJoinDto dto, Squad squad) {
        if (!squad.getCrew().getName().equals(new Name(dto.crewName()))) {
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

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOTFOUND, memberId));
    }

    private void persistSquadIfCrewMemberIsValid(SquadCreateDto dto, Member member, Crew crew) {
        crew.getCrewMembers().stream()
                .filter(crewMember -> crewMember.getMember().equals(member))
                .findFirst()
                .ifPresentOrElse(
                        crewMember -> persistSquadAndRegisterAdmin(dto, crewMember, crew),
                        () -> { throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT); }
                );
    }

    private void persistSquadAndRegisterAdmin(SquadCreateDto dto, CrewMember crewMember, Crew crew) {
        Squad squad = dto.toEntity(crewMember, crew);
        squad.addSquadMember(SquadMember.forLeader(crewMember, LocalDateTime.now()));
        Squad persistedSquad = squadRepository.save(squad);
        List<CategoryType> categoryTypes = CategoryTypeUtil.extractPossible(CategoryType.fromTexts(dto.categories()));
        List<Category> categories = Category.fromCategoryTypes(categoryTypes);
        squadRepository.batchInsertSquadCategories(persistedSquad.getId(), categories);
    }
}
