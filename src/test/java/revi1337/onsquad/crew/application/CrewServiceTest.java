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
import revi1337.onsquad.crew.dto.CrewCreateDto;
import revi1337.onsquad.crew.dto.CrewJoinDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.image.domain.vo.SupportAttachmentType;
import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.*;
import revi1337.onsquad.participant.domain.CrewParticipant;
import revi1337.onsquad.participant.domain.CrewParticipantRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrewService 테스트")
class CrewServiceTest {

    @Mock private CrewRepository crewRepository;
    @Mock private CrewParticipantRepository crewParticipantRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private S3BucketUploader s3BucketUploader;
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
                softly.assertThat(duplicated).isTrue();
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
                softly.assertThat(duplicated).isFalse();
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
            byte[] pngImage = SupportAttachmentType.PNG.getMagicByte();
            String imageRemoteAddress = "[Remote Address]";
            String imageName = "imageName";
            Member member = Member.builder().id(memberId).build();
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(crewRepository.findByName(new Name(crewCreateDto.name())))
                    .willReturn(Optional.of(crewCreateDto.toEntity(new Image(imageRemoteAddress), member)));

            // when & then
            assertThatThrownBy(() -> crewService.createNewCrew(memberId, crewCreateDto, pngImage, imageName))
                    .isInstanceOf(CrewBusinessException.AlreadyExists.class)
                    .hasMessage(String.format("%s 크루가 이미 존재하여 크루를 개설할 수 없습니다.", crewCreateDto.name()));
        }

        @Test
        @DisplayName("특정한 이름의 Crew 가 없으면 크루를 생성할 수 있다. (v1)")
        public void createNewCrew2() {
            // given
            CrewCreateDto crewCreateDto = new CrewCreateDto("크루 이름", "크루 소개", "크루 디테일", List.of("태그1", "테그2"), "카카오링크");
            Long memberId = 1L;
            byte[] pngImage = SupportAttachmentType.PNG.getMagicByte();
            String imageName = "imageName";
            String imageUrl = "[default image url]";
            Member member = Member.builder().id(memberId).build();
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(crewRepository.findByName(new Name(crewCreateDto.name()))).willReturn(Optional.empty());
            given(s3BucketUploader.uploadCrew(pngImage, imageName)).willReturn(imageUrl);

            // when
            crewService.createNewCrew(memberId, crewCreateDto, pngImage, imageName);

            // then
            then(crewRepository).should(times(1)).save(any(Crew.class));
        }

        @Test
        @DisplayName("특정한 이름의 Crew 가 없으면 크루를 생성할 수 있다. (v2)")
        public void createNewCrew3() {
            // given
            CrewCreateDto crewCreateDto = new CrewCreateDto("크루 이름", "크루 소개", "크루 디테일", List.of("태그1", "테그2"), "카카오링크");
            Long memberId = 1L;
            byte[] pngImage = SupportAttachmentType.PNG.getMagicByte();
            String imageRemoteAddress = "[Remote Address]";
            String imageName = "imageName";
            String imageUrl = "[default image url]";
            Member member = Member.builder().id(memberId).build();
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(crewRepository.findByName(new Name(crewCreateDto.name()))).willReturn(Optional.empty());
            given(crewRepository.save(any(Crew.class))).willReturn(crewCreateDto.toEntity(new Image(imageRemoteAddress), member));
            given(s3BucketUploader.uploadCrew(pngImage, imageName)).willReturn(imageUrl);

            // when
            crewService.createNewCrew(memberId, crewCreateDto, pngImage, imageName);

            // then
            then(crewRepository).should(times(1)).save(any(Crew.class));
        }
    }

    @Nested
    @DisplayName("Crew 가입 신청을 테스트한다.")
    class JoinCrew {

        @Test
        @DisplayName("Crew 생성자와 참가 신청한 Member 가 같으면 예외를 던진다.")
        public void joinCrew1() {
            Long memberId = 1L;
            Member member = createMember(memberId);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            Crew crew = createCrew(1L, "크루 이름", member);
            CrewJoinDto crewJoinDto = new CrewJoinDto(crew.getName().getValue());
            given(crewRepository.findByNameWithCrewMembers(crew.getName())).willReturn(Optional.of(crew));

            // when & then
            assertThatThrownBy(() -> crewService.joinCrew(memberId, crewJoinDto))
                    .isInstanceOf(CrewBusinessException.OwnerCantParticipant.class)
                    .hasMessage("크루를 만든 사람은 신청할 수 없습니다.");
        }

        @Test
        @DisplayName("Member 가 이미 Crew 에 소속되어 있다면 예외를 던진다.")
        public void joinCrew2() {
            Long memberId = 1L;
            Member member = createMember(memberId);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            Crew crew = createCrew(1L, "크루 이름", createMember(2L));
            crew.addCrewMember(createCrewMember(crew, member));
            given(crewRepository.findByNameWithCrewMembers(crew.getName())).willReturn(Optional.of(crew));
            CrewJoinDto crewJoinDto = new CrewJoinDto(crew.getName().getValue());

            // when & then
            assertThatThrownBy(() -> crewService.joinCrew(memberId, crewJoinDto))
                    .isInstanceOf(CrewBusinessException.AlreadyJoin.class)
                    .hasMessage(String.format("이미 %s 크루에 가입된 사용자입니다.", crew.getName().getValue()));
        }

        @Test
        @DisplayName("이미 Crew 에 참여신청한 이력이 있다면 참가 요청 정보를 update 한다.")
        public void joinCrew3() {
            Long memberId = 1L;
            Member member = createMember(memberId);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            Crew crew = createCrew(1L, "크루 이름", createMember(2L));
            given(crewRepository.findByNameWithCrewMembers(crew.getName())).willReturn(Optional.of(crew));
            CrewParticipant crewParticipant = createCrewParticipant(crew, member);
            given(crewParticipantRepository.findByCrewIdAndMemberIdUsingLock(crew.getId(), memberId)).willReturn(Optional.of(crewParticipant));
            CrewJoinDto crewJoinDto = new CrewJoinDto(crew.getName().getValue());

            // when
            crewService.joinCrew(memberId, crewJoinDto);

            // then
            then(crewParticipantRepository).should(times(1)).saveAndFlush(crewParticipant);
        }

        @Test
        @DisplayName("Crew 에 참가 신청을 성공한다.")
        public void joinCrew4() {
            Long memberId = 1L;
            Member member = createMember(memberId);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            Crew crew = createCrew(1L, "크루 이름", createMember(2L));
            given(crewRepository.findByNameWithCrewMembers(crew.getName())).willReturn(Optional.of(crew));
            given(crewParticipantRepository.findByCrewIdAndMemberIdUsingLock(crew.getId(), memberId)).willReturn(Optional.empty());
            CrewJoinDto crewJoinDto = new CrewJoinDto(crew.getName().getValue());

            // when
            crewService.joinCrew(memberId, crewJoinDto);

            // then
            then(crewParticipantRepository).should(times(1)).save(any(CrewParticipant.class));
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
