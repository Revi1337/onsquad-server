package revi1337.onsquad.crew.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewCreateDto;
import revi1337.onsquad.crew.dto.CrewJoinDto;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("CrewService 테스트")
class CrewServiceTest {

    @Mock private CrewRepository crewRepository;
    @Mock private CrewMemberRepository crewMemberRepository;
    @Mock private MemberRepository memberRepository;
    @InjectMocks private CrewService crewService;

    @Nested
    @DisplayName("checkDuplicateNickname 메소드를 테스트한다.")
    class CheckDuplicateNickname {

        @Test
        @DisplayName("Crew 이름으로 검색된 Crew 가 존재하면 true 를 반환한다.")
        public void checkDuplicateNickname() {
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
        public void checkDuplicateNickname2() {
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
    }

    @Nested
    @DisplayName("createNewCrew 메소드를 테스트한다.")
    class CreateNewCrew {
        
        @Test
        @DisplayName("특정한 이름의 Crew 가 있으면 크루를 생성할 수 없다.")
        public void createNewCrew1() {
            // given
            CrewCreateDto crewCreateDto = new CrewCreateDto("크루 이름", "크루 소개", "크루 디테일", List.of("태그1", "테그2"), "카카오링크");
            Long memberId = 1L;
            byte[] pngImage = ImageFactory.PNG_IMAGE;
            Member member = Member.builder().id(memberId).build();

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(crewRepository.findByName(new Name(crewCreateDto.name())))
                    .willReturn(Optional.of(crewCreateDto.toEntity(new Image(pngImage), member)));

            // when & then
            assertThatThrownBy(() -> crewService.createNewCrew(crewCreateDto, memberId, pngImage))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("크루명이 이미 존재하여 Crew 를 개설할 수 없습니다.");
        }

        @Test
        @DisplayName("특정한 이름의 Crew 가 없으면 크루를 생성할 수 있다. (v1)")
        public void createNewCrew2() {
            // given
            CrewCreateDto crewCreateDto = new CrewCreateDto("크루 이름", "크루 소개", "크루 디테일", List.of("태그1", "테그2"), "카카오링크");
            Long memberId = 1L;
            byte[] pngImage = ImageFactory.PNG_IMAGE;
            Member member = Member.builder().id(memberId).build();
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(crewRepository.findByName(new Name(crewCreateDto.name()))).willReturn(Optional.empty());

            // when
            crewService.createNewCrew(crewCreateDto, memberId, pngImage);

            // then
            then(crewRepository).should(times(1)).save(any(Crew.class));
        }

        @Test
        @DisplayName("특정한 이름의 Crew 가 없으면 크루를 생성할 수 있다. (v2)")
        public void createNewCrew3() {
            // given
            CrewCreateDto crewCreateDto = new CrewCreateDto("크루 이름", "크루 소개", "크루 디테일", List.of("태그1", "테그2"), "카카오링크");
            Long memberId = 1L;
            byte[] pngImage = ImageFactory.PNG_IMAGE;
            Member member = Member.builder().id(memberId).build();
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(crewRepository.findByName(new Name(crewCreateDto.name()))).willReturn(Optional.empty());
            given(crewRepository.save(any(Crew.class))).willReturn(crewCreateDto.toEntity(new Image(pngImage), member));

            // when
            crewService.createNewCrew(crewCreateDto, memberId, pngImage);

            // then
            then(crewRepository).should(times(1)).save(any(Crew.class));
        }
    }

    @Nested
    @DisplayName("findCrewByName 메소드를 테스트한다.")
    class FindCrewByName {

        @Test
        @DisplayName("크루 이름에 해당하는 게시글이 없으면 실패한다.")
        public void findCrewByName() {
            // given
            String name = "크루 이름";
            Name crewName = new Name(name);
            given(crewRepository.findCrewByName(crewName)).willReturn(Optional.empty());

            // when
            Throwable throwable = catchThrowable(() -> crewService.findCrewByName(name));

            // then
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("해당 이름의 크루가 존재하지 않습니다.");
            then(crewRepository).should(times(1)).findCrewByName(crewName);
        }
    }

    @Nested
    @DisplayName("joinCrew 메소드를 테스트한다.")
    class JoinCrew {

        @Test
        @DisplayName("Member 가 Crew 에 가입신청을 했지만 PENDING 이면 실패한다.")
        public void joinCrew1() {
            Long memberId = 1L;
            Member member = Member.builder().id(memberId).build();
            Crew crew = Crew.builder().name(CrewFactory.NAME).member(member).build();
            CrewMember crewMember = CrewMember.of(crew, member, JoinStatus.PENDING);
            CrewJoinDto crewJoinDto = new CrewJoinDto(crew.getName().getValue());
            given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));
            given(crewRepository.findByName(eq(CrewFactory.NAME))).willReturn(Optional.of(crew));
            given(crewMemberRepository.findCrewMemberByMemberId(eq(memberId))).willReturn(Optional.of(crewMember));

            // when & then
            assertThatThrownBy(() -> crewService.joinCrew(crewJoinDto, memberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 해당 크루에 가입신청을 하였습니다.");
        }

        @Test
        @DisplayName("Member 가 Crew 에 가입신청을 했지만 ACCEPT 면 실패한다.")
        public void joinCrew2() {
            Long memberId = 1L;
            Member member = Member.builder().id(memberId).build();
            Crew crew = Crew.builder().name(CrewFactory.NAME).member(member).build();
            CrewMember crewMember = CrewMember.of(crew, member, JoinStatus.ACCEPT);
            CrewJoinDto crewJoinDto = new CrewJoinDto(crew.getName().getValue());
            given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));
            given(crewRepository.findByName(eq(CrewFactory.NAME))).willReturn(Optional.of(crew));
            given(crewMemberRepository.findCrewMemberByMemberId(eq(memberId))).willReturn(Optional.of(crewMember));

            // when & then
            assertThatThrownBy(() -> crewService.joinCrew(crewJoinDto, memberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 해당 크루에 가입된 사용자입니다.");
        }

        @Test
        @DisplayName("Member 가 Crew 에 가입신청을 한적 없으면 성공한다.")
        public void joinCrew3() {
            // given
            Long memberId = 1L;
            Member member = Member.builder().id(memberId).build();
            Crew crew = Crew.builder().name(CrewFactory.NAME).member(member).build();
            CrewJoinDto crewJoinDto = new CrewJoinDto(crew.getName().getValue());
            given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));
            given(crewRepository.findByName(eq(CrewFactory.NAME))).willReturn(Optional.of(crew));
            given(crewMemberRepository.findCrewMemberByMemberId(eq(memberId))).willReturn(Optional.empty());

            // when
            crewService.joinCrew(crewJoinDto, memberId);

            // then
            then(crewMemberRepository).should(times(1)).save(any(CrewMember.class));
        }

        @Test
        @DisplayName("Member 가 가입신청할 Crew 가 존재하지 않으면 실패한다.")
        public void joinCrew4() {
            // given
            Long memberId = 1L;
            Member member = Member.builder().id(memberId).build();
            Crew crew = Crew.builder().name(CrewFactory.NAME).member(member) .build();
            CrewJoinDto crewJoinDto = new CrewJoinDto(crew.getName().getValue());
            given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));
            given(crewRepository.findByName(eq(CrewFactory.NAME))).willReturn(Optional.empty());

            // when && then
            assertThatThrownBy(() -> crewService.joinCrew(crewJoinDto, memberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("크루가 존재하지 않아 크루에 가입신청을 할 수 없습니다.");
        }
    }
}