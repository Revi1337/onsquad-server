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
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.dto.SquadCreateDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@DisplayName("Squad 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SquadServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private SquadRepository squadRepository;
    @Mock private CrewRepository crewRepository;
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
}
