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
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.application.dto.CrewAcceptDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.image.domain.vo.SupportAttachmentType;
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.*;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrewConfigService 테스트")
class CrewConfigServiceTest {

    @Mock private CrewRepository crewRepository;
    @Mock private CrewMemberJpaRepository crewMemberRepository;
    @Mock private CrewParticipantJpaRepository crewParticipantRepository;
    @Mock private S3BucketUploader s3BucketUploader;
    @InjectMocks private CrewConfigService crewConfigService;

    @Nested
    @DisplayName("Crew 모집 게시글을 테스트한다.")
    class UpdateCrew {

        @Test
        @DisplayName("Crew 게시글 작성자와 업데이트 요청 Member 가 다르면 예외를 던진다.")
        public void updateCrew1() {
            // given
            String targetCrewName = "변경하려는 크루 이름";
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("변경 크루 이름", "변경 크루 소개", "변경 크루 디테일", List.of("태그1", "태그2"), "카카오링크");
            Long memberId = 1L;
            byte[] image = SupportAttachmentType.PNG.getMagicByte();
            String testImageName = "테스트 이미지 이름";
            Crew crew = createCrew(1L, createMember(2L), "이미지 주소", "카카오 링크");
            given(crewRepository.getCrewByNameWithImage(new Name(targetCrewName))).willReturn(crew);

            // when && then
            assertThatThrownBy(() -> crewConfigService.updateCrew(memberId, targetCrewName, crewUpdateDto, image, testImageName))
                    .isExactlyInstanceOf(CrewBusinessException.InvalidPublisher.class);
        }

        @Test
        @DisplayName("Crew 게시글 업데이트에 성공한다.")
        public void updateCrew2() {
            // given
            String targetCrewName = "변경하려는 크루 이름";
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("변경 크루 이름", "변경 크루 소개", "변경 크루 디테일", List.of("태그1", "태그2"), "카카오링크");
            Long memberId = 1L;
            byte[] image = SupportAttachmentType.PNG.getMagicByte();
            String testImageUrl = "테스트 이미지 이름";
            Crew crew = createCrew(1L, createMember(memberId), "이미지 주소", "카카오 링크");
            given(crewRepository.getCrewByNameWithImage(new Name(targetCrewName))).willReturn(crew);
            willDoNothing().given(s3BucketUploader).updateImage(crew.getImage().getImageUrl(), image, testImageUrl);

            // when
            crewConfigService.updateCrew(memberId, targetCrewName, crewUpdateDto, image, testImageUrl);

            // then
            assertSoftly(softly -> {
                then(crewRepository).should(times(1)).saveAndFlush(crew);
                softly.assertThat(crew.getName().getValue()).isEqualTo("변경 크루 이름");
                softly.assertThat(crew.getIntroduce().getValue()).isEqualTo("변경 크루 소개");
                softly.assertThat(crew.getDetail().getValue()).isEqualTo("변경 크루 디테일");
                softly.assertThat(crew.getHashTags().getValue()).isEqualTo("태그1,태그2");
                softly.assertThat(crew.getKakaoLink()).isEqualTo("카카오링크");
            });
        }

        @Test
        @DisplayName("기존 Crew 게시글의 KakaoLink null 이어도 Update 할 수 있다.")
        public void updateCrew3() {
            // given
            String targetCrewName = "변경하려는 크루 이름";
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("변경 크루 이름", "변경 크루 소개", "변경 크루 디테일", List.of("태그1", "태그2"), "변경 카카오톡 링크");
            Long memberId = 1L;
            byte[] image = SupportAttachmentType.PNG.getMagicByte();
            String testImageUrl = "테스트 이미지 이름";
            Crew crew = createCrew(1L, createMember(memberId), "이미지 주소", null);
            given(crewRepository.getCrewByNameWithImage(new Name(targetCrewName))).willReturn(crew);
            willDoNothing().given(s3BucketUploader).updateImage(crew.getImage().getImageUrl(), image, testImageUrl);

            // when
            crewConfigService.updateCrew(memberId, targetCrewName, crewUpdateDto, image, testImageUrl);

            // then
            assertSoftly(softly -> {
                then(crewRepository).should(times(1)).saveAndFlush(crew);
                softly.assertThat(crew.getName().getValue()).isEqualTo("변경 크루 이름");
                softly.assertThat(crew.getIntroduce().getValue()).isEqualTo("변경 크루 소개");
                softly.assertThat(crew.getDetail().getValue()).isEqualTo("변경 크루 디테일");
                softly.assertThat(crew.getHashTags().getValue()).isEqualTo("태그1,태그2");
                softly.assertThat(crew.getKakaoLink()).isEqualTo("변경 카카오톡 링크");
            });
        }

        @Test
        @DisplayName("기존 Crew 게시글의 KakaoLink null 이고, 변경 링크가 null 이어도 성공한다.")
        public void updateCrew4() {
            // given
            String targetCrewName = "변경하려는 크루 이름";
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("변경 크루 이름", "변경 크루 소개", "변경 크루 디테일", List.of("태그1", "태그2"), null);
            Long memberId = 1L;
            byte[] image = SupportAttachmentType.PNG.getMagicByte();
            String testImageUrl = "테스트 이미지 이름";
            Crew crew = createCrew(1L, createMember(memberId), "이미지 주소", null);
            given(crewRepository.getCrewByNameWithImage(new Name(targetCrewName))).willReturn(crew);
            willDoNothing().given(s3BucketUploader).updateImage(crew.getImage().getImageUrl(), image, testImageUrl);

            // when
            crewConfigService.updateCrew(memberId, targetCrewName, crewUpdateDto, image, testImageUrl);

            // then
            assertSoftly(softly -> {
                then(crewRepository).should(times(1)).saveAndFlush(crew);
                softly.assertThat(crew.getName().getValue()).isEqualTo("변경 크루 이름");
                softly.assertThat(crew.getIntroduce().getValue()).isEqualTo("변경 크루 소개");
                softly.assertThat(crew.getDetail().getValue()).isEqualTo("변경 크루 디테일");
                softly.assertThat(crew.getHashTags().getValue()).isEqualTo("태그1,태그2");
                softly.assertThat(crew.getKakaoLink()).isNull();
            });
        }

        @Test
        @DisplayName("기존 Crew 게시글의 HashTag 가 null 이어도 Update 할 수 있다.")
        public void updateCrew5() {
            // given
            String targetCrewName = "변경하려는 크루 이름";
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("변경 크루 이름", "변경 크루 소개", "변경 크루 디테일", List.of("태그1", "태그2"), "변경 카카오톡 링크");
            Long memberId = 1L;
            byte[] image = SupportAttachmentType.PNG.getMagicByte();
            String testImageUrl = "테스트 이미지 이름";
            Crew crew = createCrew(1L, createMember(memberId), "이미지 주소", null, "카카오톡 링크");
            given(crewRepository.getCrewByNameWithImage(new Name(targetCrewName))).willReturn(crew);
            willDoNothing().given(s3BucketUploader).updateImage(crew.getImage().getImageUrl(), image, testImageUrl);

            // when
            crewConfigService.updateCrew(memberId, targetCrewName, crewUpdateDto, image, testImageUrl);

            // then
            assertSoftly(softly -> {
                then(crewRepository).should(times(1)).saveAndFlush(crew);
                softly.assertThat(crew.getName().getValue()).isEqualTo("변경 크루 이름");
                softly.assertThat(crew.getIntroduce().getValue()).isEqualTo("변경 크루 소개");
                softly.assertThat(crew.getDetail().getValue()).isEqualTo("변경 크루 디테일");
                softly.assertThat(crew.getHashTags().getValue()).isEqualTo("태그1,태그2");
                softly.assertThat(crew.getKakaoLink()).isEqualTo("변경 카카오톡 링크");
            });
        }

        @Test
        @DisplayName("기존 Crew 게시글의 HashTag 가 null 이고, 변경 HashTag 가 null 이어도 Update 할 수 있다.")
        public void updateCrew6() {
            // given
            String targetCrewName = "변경하려는 크루 이름";
            CrewUpdateDto crewUpdateDto = new CrewUpdateDto("변경 크루 이름", "변경 크루 소개", "변경 크루 디테일", null, "변경 카카오톡 링크");
            Long memberId = 1L;
            byte[] image = SupportAttachmentType.PNG.getMagicByte();
            String testImageUrl = "테스트 이미지 이름";
            Crew crew = createCrew(1L, createMember(memberId), "이미지 주소", null, "카카오톡 링크");
            given(crewRepository.getCrewByNameWithImage(new Name(targetCrewName))).willReturn(crew);
            willDoNothing().given(s3BucketUploader).updateImage(crew.getImage().getImageUrl(), image, testImageUrl);

            // when
            crewConfigService.updateCrew(memberId, targetCrewName, crewUpdateDto, image, testImageUrl);

            // then
            assertSoftly(softly -> {
                then(crewRepository).should(times(1)).saveAndFlush(crew);
                softly.assertThat(crew.getName().getValue()).isEqualTo("변경 크루 이름");
                softly.assertThat(crew.getIntroduce().getValue()).isEqualTo("변경 크루 소개");
                softly.assertThat(crew.getDetail().getValue()).isEqualTo("변경 크루 디테일");
                softly.assertThat(crew.getHashTags().getValue()).isEqualTo("[EMPTY]");
                softly.assertThat(crew.getKakaoLink()).isEqualTo("변경 카카오톡 링크");
            });
        }
    }

    @Nested
    @DisplayName("Crew 참가 수락을 테스트한다.")
    class AcceptCrewMember {

        @Test
        @DisplayName("수락할 Crew 가 존재해도 작성자가 다르면 실패한다.")
        public void acceptCrewMember1() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L);
            Member member = createMember(2L);
            Crew crew = createCrew(1L, member);
            given(crewRepository.getByName(new Name(crewAcceptDto.requestCrewName()))).willReturn(crew);

            // when & then
            assertThatThrownBy(() -> crewConfigService.acceptCrewMember(memberId, crewAcceptDto))
                    .isInstanceOf(CrewBusinessException.InvalidPublisher.class)
                    .hasMessage(String.format("%s 크루 작성자와 일치하지 않습니다.", crewAcceptDto.requestCrewName()));
        }

        @Test
        @DisplayName("요청자가 Crew 에 참여 요청을 한 적 없다면, 예외를 던진다.")
        public void acceptCrewMember2() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L);
            Member member = createMember(1L);
            Crew crew = createCrew(1L, member);
            given(crewRepository.getByName(new Name(crewAcceptDto.requestCrewName()))).willReturn(crew);
            given(crewParticipantRepository.findByCrewIdAndMemberId(crew.getId(), memberId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> crewConfigService.acceptCrewMember(memberId, crewAcceptDto))
                    .isInstanceOf(CrewMemberBusinessException.NeverRequested.class)
                    .hasMessage("크루에 참여요청을 한 이력이 없습니다.", crewAcceptDto.requestCrewName());
        }

        @Test
        @DisplayName("요청자가 Crew 에 참여 요청을 한 적 없다면, 예외를 던진다.")
        public void acceptCrewMember3() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L);
            Member member = createMember(1L);
            Crew crew = createCrew(1L, member);
            given(crewRepository.getByName(new Name(crewAcceptDto.requestCrewName()))).willReturn(crew);
            given(crewParticipantRepository.findByCrewIdAndMemberId(crew.getId(), memberId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> crewConfigService.acceptCrewMember(memberId, crewAcceptDto))
                    .isInstanceOf(CrewMemberBusinessException.NeverRequested.class)
                    .hasMessage("크루에 참여요청을 한 이력이 없습니다.", crewAcceptDto.requestCrewName());
        }

        @Test
        @DisplayName("요청자가 Crew 에 참여 요청을 한 적 없다면, 예외를 던진다.")
        public void acceptCrewMember4() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L);
            Member member = createMember(1L);
            Crew crew = createCrew(1L, member);
            given(crewRepository.getByName(new Name(crewAcceptDto.requestCrewName()))).willReturn(crew);
            given(crewParticipantRepository.findByCrewIdAndMemberId(crew.getId(), memberId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> crewConfigService.acceptCrewMember(memberId, crewAcceptDto))
                    .isInstanceOf(CrewMemberBusinessException.NeverRequested.class)
                    .hasMessage("크루에 참여요청을 한 이력이 없습니다.", crewAcceptDto.requestCrewName());
        }

        @Test
        @DisplayName("요청자가 Crew 에 참여 요청을 한 적 있다면, Crew 에 Member 를 가입시킨다.")
        public void acceptCrewMember5() {
            // given
            Long memberId = 1L;
            CrewAcceptDto crewAcceptDto = new CrewAcceptDto("크루 이름", 1L);
            Member member = createMember(1L);
            Crew crew = createCrew(1L, member);
            given(crewRepository.getByName(new Name(crewAcceptDto.requestCrewName()))).willReturn(crew);
            Member testMember = createMember(2L);
            CrewParticipant crewParticipant = createCrewParticipant(crew, testMember);
            given(crewParticipantRepository.findByCrewIdAndMemberId(crew.getId(), memberId))
                    .willReturn(Optional.of(crewParticipant));

            // when
            crewConfigService.acceptCrewMember(memberId, crewAcceptDto);

            // then
            assertSoftly(softly -> {
                then(crewParticipantRepository).should(times(1)).delete(crewParticipant);
                then(crewMemberRepository).should(times(1)).save(any(CrewMember.class));
            });
        }
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

    private static Crew createCrew(Long id, Member member, String imageRemoteAddress, String kakaoLink) {
        return Crew.builder()
                .id(id)
                .name(new Name("Crew 명"))
                .introduce(new Introduce("Crew 소개"))
                .detail(new Detail("Crew 세부정보"))
                .hashTags(new HashTags(List.of("해시태그1", "해시태그2", "해시태그3")))
                .kakaoLink(kakaoLink)
                .member(member)
                .image(new Image(imageRemoteAddress))
                .build();
    }

    private static Crew createCrew(Long id, Member member, String imageRemoteAddress, List<String> hashTag, String kakaoLink) {
        return Crew.builder()
                .id(id)
                .name(new Name("Crew 명"))
                .introduce(new Introduce("Crew 소개"))
                .detail(new Detail("Crew 세부정보"))
                .hashTags(new HashTags(hashTag))
                .kakaoLink(kakaoLink)
                .member(member)
                .image(new Image(imageRemoteAddress))
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

    public static Crew createCrew(Long id, String name, Member member) {
        return Crew.builder()
                .id(id)
                .name(new Name(name))
                .introduce(new Introduce("Crew 소개"))
                .detail(new Detail("Crew 세부정보"))
                .hashTags(new HashTags(List.of("해시태그1", "해시태그2", "해시태그3")))
                .kakaoLink("카카오 오픈채팅 링크")
                .member(member)
                .build();
    }

    public static CrewParticipant createCrewParticipant(Crew crew, Member member) {
        return CrewParticipant.of(crew, member, LocalDateTime.now());
    }

    public static CrewMember createCrewMember(Crew crew, Member member) {
        return CrewMember.builder()
                .crew(crew)
                .member(member)
                .build();
    }
}
