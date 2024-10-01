package revi1337.onsquad.squad.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.category.domain.CategoryRepository;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.member.domain.vo.*;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.squad_participant.domain.SquadParticipant;
import revi1337.onsquad.squad_participant.domain.SquadParticipantJpaRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.domain.vo.*;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.application.dto.SquadJoinDto;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.SquadMember;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static revi1337.onsquad.crew_member.domain.vo.JoinStatus.PENDING;
import static revi1337.onsquad.squad_member.domain.vo.SquadRole.*;

@ExtendWith(MockitoExtension.class)
class SquadServiceTest {

    @Mock private MemberJpaRepository memberRepository;
    @Mock private SquadRepository squadRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private CrewRepository crewRepository;
    @Mock private SquadParticipantJpaRepository squadParticipantRepository;
    @InjectMocks private SquadService squadService;

    @Test
    @DisplayName("Squad 가 생성되는지 확인한다.")
    public void createNewSquad() {
        // given
        SquadCreateDto squadCreateDto = new SquadCreateDto("크루 1", "스쿼드 이름", "스쿼드 내용", 8, "주소", "상세주소", List.of("등산"), "카카오링크", "디스코드링크");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        CrewMember crewMember = CrewMember.of(crew, member);
        crew.getCrewMembers().add(crewMember);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewRepository.findByNameWithCrewMembers(new Name(squadCreateDto.crewName()))).willReturn(Optional.of(crew));
        given(squadRepository.save(any(Squad.class))).willReturn(squadCreateDto.toEntity(crewMember, crew));
        List<CategoryType> categoryTypes = CategoryType.fromTexts(squadCreateDto.categories());
        List<Category> categories = Category.fromCategoryTypes(categoryTypes);
        given(categoryRepository.findCategoriesInSecondCache(categoryTypes)).willReturn(categories);

        // when
        squadService.createNewSquad(squadCreateDto, memberId);

        // then
        then(squadRepository).should(times(1)).save(any(Squad.class));
        then(squadRepository).should(times(1)).batchInsertSquadCategories(any(), eq(categories));
    }

    @Test
    @DisplayName("Squad 를 생성하기 전에 Member 가 없으면 예외를 던진다.")
    public void createNewSquad2() {
        // given
        SquadCreateDto squadCreateDto = new SquadCreateDto("크루 1", "스쿼드 이름", "스쿼드 내용", 8, "주소", "상세주소", List.of("등산"), "카카오링크", "디스코드링크");
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> squadService.createNewSquad(squadCreateDto, memberId))
                .isInstanceOf(MemberBusinessException.NotFound.class)
                .hasMessage("id 가 1 인 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("Squad 를 생성하기 전에 Crew 가 없으면 예외를 던진다.")
    public void createNewSquad3() {
        // given
        SquadCreateDto squadCreateDto = new SquadCreateDto("크루 1", "스쿼드 이름", "스쿼드 내용", 8, "주소", "상세주소", List.of("등산"), "카카오링크", "디스코드링크");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewRepository.findByNameWithCrewMembers(new Name(squadCreateDto.crewName()))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> squadService.createNewSquad(squadCreateDto, memberId))
                .isInstanceOf(CrewBusinessException.NotFoundByName.class)
                .hasMessage("크루 1 크루 게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("Squad 를 생성하기 전에 Crew 에 Member 가 속해있지 않으면 예외를 던진다.")
    public void createNewSquad4() {
        // given
        SquadCreateDto squadCreateDto = new SquadCreateDto("크루 1", "스쿼드 이름", "스쿼드 내용", 8, "주소", "상세주소", List.of("등산"), "카카오링크", "디스코드링크");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewRepository.findByNameWithCrewMembers(new Name(squadCreateDto.crewName()))).willReturn(Optional.of(crew));

        // when & then
        assertThatThrownBy(() -> squadService.createNewSquad(squadCreateDto, memberId))
                .isInstanceOf(CrewMemberBusinessException.NotParticipant.class)
                .hasMessage("사용자는 크루 1 크루에 속해있지 않습니다.");
    }

    @Test
    @DisplayName("Squad 참가 요청에 성공한다.")
    public void joinSquad1() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("Crew 명", 1L);
        Long requestMemberId = 2L;
        Member findMember = createMember(1L);
        given(memberRepository.findById(requestMemberId)).willReturn(Optional.of(findMember));
        Crew findCrew = createCrew(1L, findMember);
        CrewMember findCrewMember = createCrewMember(1L, findCrew, findMember);
        findCrew.addCrewMember(findCrewMember);
        given(crewRepository.findByNameWithCrewMembers(new Name(squadJoinDto.crewName()))).willReturn(Optional.of(findCrew));
        Squad findSquad = createSquad(1L, findCrew, createCrewMember(2L, createCrew(2L), createMember(3L)));
        given(squadRepository.getSquadByIdWithSquadMembers(squadJoinDto.squadId())).willReturn(findSquad);
        given(squadParticipantRepository.findBySquadIdAndCrewMemberId(anyLong(), anyLong())).willReturn(Optional.empty());

        // when
        squadService.submitParticipationRequest(squadJoinDto, requestMemberId);

        // then
        then(squadParticipantRepository).should(times(1)).save(any(SquadParticipant.class));
    }

    @Test
    @DisplayName("Member 가 Crew 에 속해있지 않으면, Squad 참가 요청에 실패한다.")
    public void joinSquad2() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("Crew 명", 1L);
        Long requestMemberId = 2L;

        Member findMember = createMember(1L);
        given(memberRepository.findById(requestMemberId)).willReturn(Optional.of(findMember));
        Crew findCrew = createCrew(1L, findMember);
        given(crewRepository.findByNameWithCrewMembers(new Name(squadJoinDto.crewName()))).willReturn(Optional.of(findCrew));

        // when & then
        assertThatThrownBy(() -> squadService.submitParticipationRequest(squadJoinDto, requestMemberId))
                .isInstanceOf(CrewMemberBusinessException.NotParticipant.class)
                .hasMessage(String.format("사용자는 %s 크루에 속해있지 않습니다.", squadJoinDto.crewName()));
    }

    @Test
    @DisplayName("Squad 를 만든 사람은 Squad 참가 요청에 실패한다.")
    public void joinSquad3() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("Crew 명", 1L);
        Long requestMemberId = 2L;

        Member findMember = createMember(1L);
        given(memberRepository.findById(requestMemberId)).willReturn(Optional.of(findMember));
        Crew findCrew = createCrew(1L, findMember);
        CrewMember findCrewMember = createCrewMember(1L, findCrew, findMember);
        findCrew.addCrewMember(findCrewMember);
        given(crewRepository.findByNameWithCrewMembers(new Name(squadJoinDto.crewName()))).willReturn(Optional.of(findCrew));
        Squad findSquad = createSquad(3L, findCrew, createCrewMember(1L));
        given(squadRepository.getSquadByIdWithSquadMembers(squadJoinDto.squadId())).willReturn(findSquad);

        // when & then
        assertThatThrownBy(() -> squadService.submitParticipationRequest(squadJoinDto, requestMemberId))
                .isExactlyInstanceOf(SquadBusinessException.OwnerCantParticipant.class)
                .hasMessage("스쿼드를 만든 사람은 신청할 수 없습니다.");
    }

    @Test
    @DisplayName("Squad 가 다른 Crew 에 속해있으면 Squad 참가 요청에 실패한다.")
    public void joinSquad4() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("Crew 명", 1L);
        Long requestMemberId = 2L;

        Member findMember = createMember(1L);
        given(memberRepository.findById(requestMemberId)).willReturn(Optional.of(findMember));
        Crew findCrew = createCrew(1L, findMember);
        CrewMember findCrewMember = createCrewMember(1L, findCrew, findMember);
        findCrew.addCrewMember(findCrewMember);
        given(crewRepository.findByNameWithCrewMembers(new Name(squadJoinDto.crewName()))).willReturn(Optional.of(findCrew));
        Squad findSquad = createSquad(1L, createCrew(1L, "dummy_name"), createCrewMember(3L));
        given(squadRepository.getSquadByIdWithSquadMembers(squadJoinDto.squadId())).willReturn(findSquad);

        // when & then
        assertThatThrownBy(() -> squadService.submitParticipationRequest(squadJoinDto, requestMemberId))
                .isExactlyInstanceOf(SquadBusinessException.NotInCrew.class)
                .hasMessage(String.format("스쿼드는 %s 크루 안에 속해있지 않습니다.", squadJoinDto.crewName()));
    }

    @Test
    @DisplayName("이미 Squad 에 속한 사용자면 Squad 참가 신청에 실패한다.")
    public void joinSquad5() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("Crew 명", 1L);
        Long requestMemberId = 2L;

        Member findMember = createMember(1L);
        given(memberRepository.findById(requestMemberId)).willReturn(Optional.of(findMember));
        Crew findCrew = createCrew(1L, findMember);
        CrewMember findCrewMember = createCrewMember(1L, findCrew, findMember);
        findCrew.addCrewMember(findCrewMember);
        given(crewRepository.findByNameWithCrewMembers(new Name(squadJoinDto.crewName()))).willReturn(Optional.of(findCrew));
        Squad findSquad = createSquad(1L, createCrew(1L), createCrewMember(3L));
        findSquad.addSquadMember(createSquadMember(1L, createCrewMember(1L)), createSquadMember(2L, createCrewMember(2L)));
        given(squadRepository.getSquadByIdWithSquadMembers(squadJoinDto.squadId())).willReturn(findSquad);

        // when & then
        assertThatThrownBy(() -> squadService.submitParticipationRequest(squadJoinDto, requestMemberId))
                .isExactlyInstanceOf(SquadBusinessException.AlreadyParticipant.class)
                .hasMessage("이미 Squad 타이틀 스쿼드에 가입된 사용자입니다.");
    }

    public static Member createMember(Long id) {
        return Member.builder()
                .id(id)
                .nickname(new Nickname("nickname"))
                .address(new Address("어딘가", "롯데캐슬"))
                .email(new Email("test@email.com"))
                .password(new Password("12345!@asa"))
                .userType(UserType.GENERAL)
                .build();
    }

    public static Crew createCrew(Long id) {
        return Crew.builder()
                .id(id)
                .name(new Name("Crew 명"))
                .introduce(new Introduce("Crew 소개"))
                .detail(new Detail("Crew 세부정보"))
                .hashTags(new HashTags(List.of("해시태그1", "해시태그2", "해시태그3")))
                .kakaoLink("카카오 오픈채팅 링크")
                .build();
    }

    public static Crew createCrew(Long id, String name) {
        return Crew.builder()
                .id(id)
                .name(new Name(name))
                .introduce(new Introduce("Crew 소개"))
                .detail(new Detail("Crew 세부정보"))
                .hashTags(new HashTags(List.of("해시태그1", "해시태그2", "해시태그3")))
                .kakaoLink("카카오 오픈채팅 링크")
                .build();
    }

    public static Crew createCrew(Long id, Member member) {
        return Crew.builder()
                .id(id)
                .name(new Name("Crew 명"))
                .introduce(new Introduce("Crew 소개"))
                .detail(new Detail("Crew 세부정보"))
                .hashTags(new HashTags(List.of("해시태그1", "해시태그2", "해시태그3")))
                .kakaoLink("카카오 오픈채팅 링크")
                .member(member)
                .build();
    }

    public static CrewMember createCrewMember(Long id) {
        return CrewMember.builder()
                .id(id)
                .build();
    }

    public static CrewMember createCrewMember(Long id, Crew crew, Member member) {
        return CrewMember.builder()
                .id(id)
                .crew(crew)
                .member(member)
                .build();
    }

    public static Squad createSquad(Long id) {
        return Squad.builder()
                .id(id)
                .title(new Title("타이틀 1"))
                .content(new Content("Sauad 내용"))
                .capacity(new Capacity(8))
                .address(new Address("주소", "상세주소"))
                .kakaoLink("카카오 오픈채팅 링크")
                .discordLink("디스코드 링크")
                .build();
    }


    public static Squad createSquad(Long id, Crew crew, CrewMember crewMember) {
        return Squad.builder()
                .id(id)
                .title(new Title("Squad 타이틀"))
                .content(new Content("Sauad 내용"))
                .capacity(new Capacity(8))
                .address(new Address("주소", "상세주소"))
                .kakaoLink("카카오 오픈채팅 링크")
                .discordLink("디스코드 링크")
                .crew(crew)
                .crewMember(crewMember)
                .build();
    }

    public static SquadMember createSquadMember(Long id) {
        return SquadMember.builder()
                .id(id)
                .role(GENERAL)
                .status(PENDING)
                .build();
    }

    public static SquadMember createSquadMember(Long id, CrewMember crewMember) {
        return SquadMember.builder()
                .id(id)
                .role(GENERAL)
                .status(PENDING)
                .crewMember(crewMember)
                .build();
    }

    public static SquadMember createSquadMember(Long id, Squad squad, CrewMember crewMember) {
        return SquadMember.builder()
                .id(id)
                .role(GENERAL)
                .status(PENDING)
                .squad(squad)
                .crewMember(crewMember)
                .build();
    }
}
