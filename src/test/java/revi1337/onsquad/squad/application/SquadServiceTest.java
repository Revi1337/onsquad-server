package revi1337.onsquad.squad.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.factory.SquadFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.squad.dto.SquadCreateDto;
import revi1337.onsquad.squad.dto.SquadDto;
import revi1337.onsquad.squad.dto.SquadJoinDto;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.SquadMember;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.*;

@DisplayName("Squad 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SquadServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private SquadRepository squadRepository;
    @Mock private CrewRepository crewRepository;
    @Mock private CrewMemberRepository crewMemberRepository;
    @InjectMocks private SquadService squadService;

    @Test
    @DisplayName("Squad 가 생성되는지 확인한다.")
    public void createNewSquad() {
        // given
        SquadCreateDto squadCreateDto = new SquadCreateDto("크루 1", "스쿼드 이름", "스쿼드 내용", 8, "주소", "상세주소", List.of("등산"), "카카오링크", "디스코드링크");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        CrewMember crewMember = CrewMember.of(crew, member, JoinStatus.ACCEPT);
        crew.getCrewMembers().add(crewMember);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewRepository.findCrewWithMembersByName(new Name(squadCreateDto.crewName()))).willReturn(Optional.of(crew));
        given(squadRepository.save(any(Squad.class))).willReturn(squadCreateDto.toEntity(member, crew));

        // when
        squadService.createNewSquad(squadCreateDto, memberId);

        // then
        then(squadRepository).should(times(1)).save(any(Squad.class));
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
        given(crewRepository.findCrewWithMembersByName(new Name(squadCreateDto.crewName()))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> squadService.createNewSquad(squadCreateDto, memberId))
                .isInstanceOf(CrewBusinessException.NotFoundByName.class)
                .hasMessage("크루 1 크루 게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("Squad 를 생성하기 전에 Member 가 Crew 에 요청했지만 대기상태면 예외를 던진다.")
    public void createNewSquad4() {
        // given
        SquadCreateDto squadCreateDto = new SquadCreateDto("크루 1", "스쿼드 이름", "스쿼드 내용", 8, "주소", "상세주소", List.of("등산"), "카카오링크", "디스코드링크");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        CrewMember crewMember = CrewMember.of(crew, member, JoinStatus.PENDING);
        crew.getCrewMembers().add(crewMember);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewRepository.findCrewWithMembersByName(new Name(squadCreateDto.crewName()))).willReturn(Optional.of(crew));

        // when & then
        assertThatThrownBy(() -> squadService.createNewSquad(squadCreateDto, memberId))
                .isInstanceOf(CrewBusinessException.AlreadyRequest.class)
                .hasMessage("크루 1 크루에 가입신청을 했지만 요청 수락 전 상태입니다.");
    }

    @Test
    @DisplayName("Squad 를 생성하기 전에 Crew 에 Member 가 속해있지 않으면 예외를 던진다.")
    public void createNewSquad5() {
        // given
        SquadCreateDto squadCreateDto = new SquadCreateDto("크루 1", "스쿼드 이름", "스쿼드 내용", 8, "주소", "상세주소", List.of("등산"), "카카오링크", "디스코드링크");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewRepository.findCrewWithMembersByName(new Name(squadCreateDto.crewName()))).willReturn(Optional.of(crew));

        // when & then
        assertThatThrownBy(() -> squadService.createNewSquad(squadCreateDto, memberId))
                .isInstanceOf(CrewMemberBusinessException.NotParticipant.class)
                .hasMessage("id 가 1 인 사용자는 크루 1 크루에 속해있지 않습니다.");
    }

    @Test
    @DisplayName("Squad 참여 요청에 성공한다.")
    public void joinSquad1() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("크루 1", 1L, "스쿼드 제목");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        Squad squad = SquadFactory.defaultSquad().crew(crew).member(member).build();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewMemberRepository.existsCrewMember(memberId)).willReturn(true);
        given(squadRepository.findSquadWithMembersById(squadJoinDto.squadId(), new Title(squadJoinDto.squadTitle()))).willReturn(Optional.of(squad));

        // when
        squadService.joinSquad(squadJoinDto, memberId);

        // then
        then(squadRepository).should(times(1)).saveAndFlush(squad);
    }

    @Test
    @DisplayName("이미 Squad 에 참여 요청을 한적있으면 실패한다.")
    public void joinSquad2() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("크루 1", 1L, "스쿼드 제목");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        Squad squad = SquadFactory.defaultSquad().crew(crew).member(member).build();
        SquadMember squadMember = SquadMember.forGeneral(member);
        squad.addSquadMember(squadMember);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewMemberRepository.existsCrewMember(memberId)).willReturn(true);
        given(squadRepository.findSquadWithMembersById(squadJoinDto.squadId(), new Title(squadJoinDto.squadTitle()))).willReturn(Optional.of(squad));

        // when & then
        assertThatThrownBy(() -> squadService.joinSquad(squadJoinDto, memberId))
                .isInstanceOf(SquadBusinessException.AlreadyRequest.class)
                .hasMessage("스쿼드 제목 스쿼드에 참여요청을 한 이력이 있습니다.");
    }

    @Test
    @DisplayName("이미 Squad 에 이미 속한 사용자면 실패한다.")
    public void joinSquad3() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("크루 1", 1L, "스쿼드 제목");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        Squad squad = SquadFactory.defaultSquad().crew(crew).member(member).build();
        SquadMember squadMember = SquadMember.forLeader(member);
        squad.addSquadMember(squadMember);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewMemberRepository.existsCrewMember(memberId)).willReturn(true);
        given(squadRepository.findSquadWithMembersById(squadJoinDto.squadId(), new Title(squadJoinDto.squadTitle()))).willReturn(Optional.of(squad));

        // when & then
        assertThatThrownBy(() -> squadService.joinSquad(squadJoinDto, memberId))
                .isInstanceOf(SquadBusinessException.AlreadyParticipant.class)
                .hasMessage("이미 스쿼드 제목 스쿼드에 가입된 사용자입니다.");
    }

    @Test
    @DisplayName("Squad 가 없으면 Squad 에 참여신청할 수 없다.")
    public void joinSquad4() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("크루 1", 1L, "스쿼드 제목");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        Squad squad = SquadFactory.defaultSquad().crew(crew).member(member).build();
        SquadMember squadMember = SquadMember.forLeader(member);
        squad.addSquadMember(squadMember);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewMemberRepository.existsCrewMember(memberId)).willReturn(true);
        given(squadRepository.findSquadWithMembersById(squadJoinDto.squadId(), new Title(squadJoinDto.squadTitle()))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> squadService.joinSquad(squadJoinDto, memberId))
                .isInstanceOf(SquadBusinessException.NotFound.class)
                .hasMessage("스쿼드 제목 스쿼드를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("Member 가 없으면 Squad 에 참여신청할 수 없다.")
    public void joinSquad5() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("크루 1", 1L, "스쿼드 제목");
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> squadService.joinSquad(squadJoinDto, memberId))
                .isInstanceOf(MemberBusinessException.NotFound.class)
                .hasMessage("id 가 1 인 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("Member 아 Crew 에 속해있지 않으면 Squad 에 참여신청할 수 없다.")
    public void joinSquad6() {
        // given
        SquadJoinDto squadJoinDto = new SquadJoinDto("크루 1", 1L, "스쿼드 제목");
        Long memberId = 1L;
        Member member = MemberFactory.defaultMember().id(memberId).build();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(crewMemberRepository.existsCrewMember(memberId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> squadService.joinSquad(squadJoinDto, memberId))
                .isInstanceOf(CrewMemberBusinessException.NotParticipant.class)
                .hasMessage("id 가 1 인 사용자는 크루 1 크루에 속해있지 않습니다.");
    }

    @Test
    @DisplayName("squadId 와 squadTitle 로 Squad 를 가져온다.")
    public void findSquad() {
        // given
        Long squadId = 1L;
        String squadTitle = SquadFactory.TITLE.getValue();
        Member member = MemberFactory.defaultMember().build();
        Squad squad = SquadFactory.defaultSquad().id(squadId).title(new Title(squadTitle)).member(member).capacity(new Capacity(8)).build();
        given(squadRepository.findSquadWithMemberByIdAndTitle(squadId, new Title(squadTitle))).willReturn(Optional.of(squad));

        // when
        SquadDto squadDto = squadService.findSquad(squadId, squadTitle);

        assertSoftly(softly -> {
            softly.assertThat(squadDto.id()).isEqualTo(squadId);
            softly.assertThat(squadDto.title()).isEqualTo(SquadFactory.TITLE.getValue());
            softly.assertThat(squadDto.capacity()).isEqualTo(8);
            softly.assertThat(squadDto.remain()).isEqualTo(8);
            softly.assertThat(squadDto.categories()).isEqualTo(List.of("배드민턴"));
        });
    }
}
