package revi1337.onsquad.crew.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewDto;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.dto.MemberDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("CrewService 테스트")
class CrewServiceTest {

    @Mock private CrewRepository crewRepository;
    @Mock private CrewMemberRepository crewMemberRepository;
    @Mock private MemberRepository memberRepository;
    @InjectMocks private CrewService crewService;

    @Test
    @DisplayName("Crew 이름으로 검색된 Crew 가 존재하면 true 를 반환한다.")
    public void createNewCrew() {
        // given
        String name = "크루 이름";
        Name crewName = new Name(name);
        given(crewRepository.existsByName(crewName)).willReturn(true);

        // when
        boolean duplicated = crewService.checkDuplicateNickname(name);

        // then
        assertSoftly(softly -> {
            then(crewRepository).should(times(1)).existsByName(crewName);
            assertThat(duplicated).isTrue();
        });
    }

    @Test
    @DisplayName("Crew 이름으로 검색된 Crew 가 존재하면 false 를 반환한다.")
    public void createNewCrew2() {
        // given
        String name = "크루 이름";
        Name crewName = new Name(name);
        given(crewRepository.existsByName(crewName)).willReturn(false);

        // when
        boolean duplicated = crewService.checkDuplicateNickname(name);

        // then
        assertSoftly(softly -> {
            then(crewRepository).should(times(1)).existsByName(crewName);
            assertThat(duplicated).isFalse();
        });
    }

    @Test
    @DisplayName("단일 Crew 게시글을 조회한다.")
    public void findCrewByName() {
        // given
        String name = "크루 이름";
        Name crewName = new Name(name);
    }

    @Test
    @DisplayName("Member 가 Crew 에 가입신청을 하지 않았으면 성공한다.")
    public void joinCrew() {
        // given
        Long memberId = 1L;
        Member member = Member.builder().id(memberId).build();
        Crew crew = Crew.builder().member(member).build();
        MemberDto memberDto = MemberDto.builder().id(memberId).build();
        CrewDto crewDto = CrewDto.of("크룸 이름", memberDto);
        given(memberRepository.findById(eq(memberId))).willReturn(Optional.ofNullable(member));
        given(crewRepository.findByName(any(Name.class))).willReturn(Optional.of(crew));
        given(crewMemberRepository.existsCrewMember(eq(memberId))).willReturn(false);

        // when
        crewService.joinCrew(crewDto);

        // then
        then(crewMemberRepository).should(times(1)).save(any(CrewMember.class));
    }

    @Test
    @DisplayName("Member 가 이미 Crew 에 가입신청을 했으면 실패한다.")
    public void joinCrew2() {
        // given
        Long memberId = 1L;
        Member member = Member.builder().id(memberId).build();
        Crew crew = Crew.builder().member(member).build();
        MemberDto memberDto = MemberDto.builder().id(memberId).build();
        CrewDto crewDto = CrewDto.of("크룸 이름", memberDto);
        given(memberRepository.findById(eq(memberId))).willReturn(Optional.ofNullable(member));
        given(crewRepository.findByName(any(Name.class))).willReturn(Optional.of(crew));
        given(crewMemberRepository.existsCrewMember(eq(memberId))).willReturn(true);

        // when && then
        assertThatThrownBy(() -> crewService.joinCrew(crewDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 크루에 가입신청을 하였습니다.");
    }

    @Test
    @DisplayName("Member 가 Crew 에 가입신청을 했는데 Crew 가 존재하지 않으면 실패한다.")
    public void joinCrew3() {
        // given
        Long memberId = 1L;
        Member member = Member.builder().id(memberId).build();
        Crew crew = Crew.builder().member(member).build();
        MemberDto memberDto = MemberDto.builder().id(memberId).build();
        CrewDto crewDto = CrewDto.of("크룸 이름", memberDto);
        given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));
        given(crewRepository.findByName(any(Name.class))).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> crewService.joinCrew(crewDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("크루가 존재하지 않아 크루에 가입신청을 할 수 없습니다.");
    }
}