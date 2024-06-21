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
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewAcceptDto;
import revi1337.onsquad.crew.dto.CrewUpdateDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.CrewMemberFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrewConfigService 테스트")
class CrewConfigServiceTest {

    @Mock private CrewRepository crewRepository;
    @Mock private CrewMemberRepository crewMemberRepository;
    @InjectMocks private CrewConfigService crewConfigService;

    @Nested
    @DisplayName("updateCrew 메소드를 테스트한다.")
    class UpdateCrew {

        @Test
        @DisplayName("Crew 게시글에 대한 정보가 없으면 Update 할 수 없다.")
        public void updateCrew1() {
            // given
            Long memberId = 1L;
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("크루 이름", "크루 소개", "크루 디테일", List.of("태그1", "테그2"), "카카오링크");
            byte[] pngImage = ImageFactory.PNG_IMAGE;
            given(crewRepository.findCrewByNameForUpdate(new Name(crewUpdateDto.name())))
                    .willReturn(Optional.empty());

            // when && then
            assertThatThrownBy(() -> crewConfigService.updateCrew(crewUpdateDto, 1L, pngImage))
                    .isInstanceOf(CrewBusinessException.NotFoundByName.class)
                    .hasMessage("크루 이름 크루 게시글이 존재하지 않습니다.");
        }

        @Test
        @DisplayName("기존 Crew 게시글의 KakaoLink null 이어도 Update 할 수 있다.")
        public void updateCrew2() {
            // given
            Long memberId = 1L;
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("크루 이름", "크루 소개", "크루 디테일", null, "카카오링크");
            byte[] pngImage = ImageFactory.PNG_IMAGE;
            Member member = MemberFactory.defaultMember().id(memberId).build();
            Crew crew = CrewFactory.defaultCrew().kakaoLink(null).member(member).image(new Image(pngImage)).build();
            given(crewRepository.findCrewByNameForUpdate(new Name(crewUpdateDto.name())))
                    .willReturn(Optional.of(crew));

            // when
            crewConfigService.updateCrew(crewUpdateDto, 1L, pngImage);

            // then
            assertSoftly(softly -> {
                then(crewRepository).should(times(1)).saveAndFlush(crew);
                assertThat(crew.getName().getValue()).isEqualTo("크루 이름");
                assertThat(crew.getIntroduce().getValue()).isEqualTo("크루 소개");
                assertThat(crew.getDetail().getValue()).isEqualTo("크루 디테일");
                assertThat(crew.getHashTags().getValue()).isEqualTo("[EMPTY]");
                assertThat(crew.getKakaoLink()).isEqualTo("카카오링크");
            });
        }

        @Test
        @DisplayName("기존 Crew 게시글의 KakaoLink 가 null 이고, 요청링크가 있어도 Update 할 수 있다.")
        public void updateCrew3() {
            // given
            Long memberId = 1L;
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("크루 이름", "크루 소개", "크루 디테일", null, "카카오링크");
            byte[] pngImage = ImageFactory.PNG_IMAGE;
            Member member = MemberFactory.defaultMember().id(memberId).build();
            Crew crew = CrewFactory.defaultCrew().kakaoLink(null).member(member).image(new Image(pngImage)).build();
            given(crewRepository.findCrewByNameForUpdate(new Name(crewUpdateDto.name())))
                    .willReturn(Optional.of(crew));

            // when
            crewConfigService.updateCrew(crewUpdateDto, 1L, pngImage);

            // then
            assertSoftly(softly -> {
                then(crewRepository).should(times(1)).saveAndFlush(crew);
                assertThat(crew.getName().getValue()).isEqualTo("크루 이름");
                assertThat(crew.getIntroduce().getValue()).isEqualTo("크루 소개");
                assertThat(crew.getDetail().getValue()).isEqualTo("크루 디테일");
                assertThat(crew.getHashTags().getValue()).isEqualTo("[EMPTY]");
                assertThat(crew.getKakaoLink()).isEqualTo("카카오링크");
            });
        }

        @Test
        @DisplayName("기존 Crew 게시글의 hashtag 가 empty 해도 업데이트 할 수 있다.")
        public void updateCrew4() {
            // given
            Long memberId = 1L;
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("크루 이름", "크루 소개", "크루 디테일", List.of("태그1", "태그2"), "카카오링크");
            byte[] pngImage = ImageFactory.PNG_IMAGE;
            Member member = MemberFactory.defaultMember().id(memberId).build();
            Crew crew = CrewFactory.defaultCrew().hashTags(new HashTags(null)).member(member).image(new Image(pngImage)).build();
            given(crewRepository.findCrewByNameForUpdate(new Name(crewUpdateDto.name())))
                    .willReturn(Optional.of(crew));

            // when
            crewConfigService.updateCrew(crewUpdateDto, 1L, pngImage);

            // then
            assertSoftly(softly -> {
                then(crewRepository).should(times(1)).saveAndFlush(crew);
                assertThat(crew.getName().getValue()).isEqualTo("크루 이름");
                assertThat(crew.getIntroduce().getValue()).isEqualTo("크루 소개");
                assertThat(crew.getDetail().getValue()).isEqualTo("크루 디테일");
                assertThat(crew.getHashTags().getValue()).isEqualTo("태그1,태그2");
                assertThat(crew.getKakaoLink()).isEqualTo("카카오링크");
            });
        }
    }

    @Nested
    @DisplayName("acceptCrewMember 메소드를 테스트한다.")
    class AcceptCrewMember {

        @Test
        @DisplayName("크루 가입요청을 수락할 Crew 가 존재하지 않으면 실패한다.")
        public void acceptCrewMember1() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L, JoinStatus.ACCEPT);
            given(crewRepository.findByName(new Name(crewAcceptDto.requestCrewName()))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> crewConfigService.acceptCrewMember(crewAcceptDto, memberId))
                    .isInstanceOf(CrewBusinessException.NotFoundByName.class)
                    .hasMessage(String.format("%s 크루 게시글이 존재하지 않습니다.", crewAcceptDto.requestCrewName()));
        }

        @Test
        @DisplayName("수락할 Crew 가 존재해도 작성자가 다르면 실패한다.")
        public void acceptCrewMember2() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L, JoinStatus.ACCEPT);
            Member member = MemberFactory.defaultMember().id(2L).build();
            Crew crew = CrewFactory.defaultCrew().member(member).build();
            given(crewRepository.findByName(new Name(crewAcceptDto.requestCrewName()))).willReturn(Optional.of(crew));

            // when & then
            assertThatThrownBy(() -> crewConfigService.acceptCrewMember(crewAcceptDto, memberId))
                    .isInstanceOf(CrewBusinessException.InvalidPublisher.class)
                    .hasMessage(String.format("%s 크루 작성자와 일치하지 않습니다.", crewAcceptDto.requestCrewName()));
        }

        @Test
        @DisplayName("수락할 Crew 가 존재해도 CrewMember 가 없으면 실패한다.")
        public void acceptCrewMember3() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L, JoinStatus.ACCEPT);
            Member member = MemberFactory.defaultMember().id(1L).build();
            Crew crew = CrewFactory.defaultCrew().id(1L).member(member).build();
            given(crewRepository.findByName(new Name(crewAcceptDto.requestCrewName())))
                    .willReturn(Optional.of(crew));
            given(crewMemberRepository.findCrewMemberByCrewIdAndMemberId(crew.getId(), memberId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> crewConfigService.acceptCrewMember(crewAcceptDto, memberId))
                    .isInstanceOf(CrewMemberBusinessException.NeverRequested.class)
                    .hasMessage(String.format("%s 크루에 참여요청을 한 이력이 없습니다.", crewAcceptDto.requestCrewName()));
        }

        @Test
        @DisplayName("Crew 와 CrewMember 그리고 요청 JoinStatus 가 Accept 라면 성공한다.")
        public void acceptCrewMember4() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L, JoinStatus.ACCEPT);
            Member member = MemberFactory.defaultMember().id(1L).build();
            Crew crew = CrewFactory.defaultCrew().id(1L).member(member).build();
            CrewMember crewMember = mock(CrewMember.class);
            given(crewRepository.findByName(new Name(crewAcceptDto.requestCrewName())))
                    .willReturn(Optional.of(crew));
            given(crewMemberRepository.findCrewMemberByCrewIdAndMemberId(crew.getId(), memberId))
                    .willReturn(Optional.of(crewMember));

            // when
            crewConfigService.acceptCrewMember(crewAcceptDto, memberId);

            // then
            then(crewMember).should(times(1)).updateStatus(crewAcceptDto.requestStatus());
        }

        @Test
        @DisplayName("Crew 와 CrewMember 가 있지만 요청 JoinStatus 가 PENDING 이면 아무일도 일어나지 않는다.")
        public void acceptCrewMember5() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L, JoinStatus.PENDING);
            Member member = MemberFactory.defaultMember().id(1L).build();
            Crew crew = CrewFactory.defaultCrew().id(1L).member(member).build();
            CrewMember crewMember = CrewMemberFactory.defaultCrewMember().crew(crew).member(member).status(JoinStatus.PENDING).build();
            given(crewRepository.findByName(new Name(crewAcceptDto.requestCrewName())))
                    .willReturn(Optional.of(crew));
            given(crewMemberRepository.findCrewMemberByCrewIdAndMemberId(crew.getId(), memberId))
                    .willReturn(Optional.of(crewMember));

            // when
            crewConfigService.acceptCrewMember(crewAcceptDto, memberId);

            // then
            then(crewRepository).shouldHaveNoMoreInteractions();
            then(crewMemberRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Crew 와 CrewMember 가 있지만 요청 JoinStatus 가 REJECT 면 CrewMember 가 삭제된다.")
        public void acceptCrewMember6() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L, JoinStatus.REJECT);
            Member member = MemberFactory.defaultMember().id(1L).build();
            Crew crew = CrewFactory.defaultCrew().id(1L).member(member).build();
            CrewMember crewMember = CrewMemberFactory.defaultCrewMember().crew(crew).member(member).status(JoinStatus.PENDING).build();
            given(crewRepository.findByName(new Name(crewAcceptDto.requestCrewName())))
                    .willReturn(Optional.of(crew));
            given(crewMemberRepository.findCrewMemberByCrewIdAndMemberId(crew.getId(), memberId))
                    .willReturn(Optional.of(crewMember));

            // when
            crewConfigService.acceptCrewMember(crewAcceptDto, memberId);

            // then
            then(crewRepository).shouldHaveNoMoreInteractions();
            then(crewMemberRepository).should(times(1)).delete(crewMember);
        }
    }
}
