package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.CHANGED_PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ENCRYPTED_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PASSWORD_VALUE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.error.exception.AuthJoinException;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.inrastructure.file.application.FileStorageManager;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepository;
import revi1337.onsquad.member.application.dto.MemberJoinDto;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;
import revi1337.onsquad.member.application.event.MemberImageDeleteEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Introduce;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

class MemberCommandServiceTest extends ApplicationLayerTestSupport {

    @SpyBean
    private MemberRepository memberRepository;

    @MockBean(name = "repositoryChain")
    private VerificationCodeRepository repositoryChain;

    @MockBean(name = "memberS3StorageManager")
    private FileStorageManager fileStorageManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private MemberCommandService memberCommandService;

    @Nested
    @DisplayName("회원가입 테스트")
    class NewMember {

        @Test
        @DisplayName("메일 인증이 되어있지 않으면 회원가입에 실패한다.")
        void newMemberTest1() {
            when(repositoryChain.isMarkedVerificationStatusWith(any(), any())).thenReturn(false);
            MemberJoinDto memberJoinDto = new MemberJoinDto(
                    REVI_EMAIL_VALUE,
                    REVI_PASSWORD_VALUE,
                    REVI_PASSWORD_VALUE,
                    REVI_NICKNAME_VALUE,
                    REVI_ADDRESS_VALUE,
                    REVI_ADDRESS_DETAIL_VALUE
            );

            assertThatThrownBy(() -> memberCommandService.newMember(memberJoinDto))
                    .isExactlyInstanceOf(AuthJoinException.NonAuthenticateEmail.class);
        }

        @Test
        @DisplayName("Nickname 이 사용중이면 실패한다.")
        void newMemberTest2() {
            memberRepository.save(REVI());
            when(repositoryChain.isMarkedVerificationStatusWith(any(), any())).thenReturn(true);
            MemberJoinDto memberJoinDto = new MemberJoinDto(
                    REVI_EMAIL_VALUE,
                    REVI_PASSWORD_VALUE,
                    REVI_PASSWORD_VALUE,
                    REVI_NICKNAME_VALUE,
                    REVI_ADDRESS_VALUE,
                    REVI_ADDRESS_DETAIL_VALUE
            );

            assertThatThrownBy(() -> memberCommandService.newMember(memberJoinDto))
                    .isExactlyInstanceOf(AuthJoinException.DuplicateNickname.class);
        }

        @Test
        @DisplayName("Email 이 사용중이면 실패한다.")
        void newMemberTest3() {
            memberRepository.save(REVI());
            when(repositoryChain.isMarkedVerificationStatusWith(any(), any())).thenReturn(true);
            MemberJoinDto memberJoinDto = new MemberJoinDto(
                    REVI_EMAIL_VALUE,
                    ANDONG_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE,
                    ANDONG_NICKNAME_VALUE,
                    ANDONG_ADDRESS_VALUE,
                    ANDONG_ADDRESS_DETAIL_VALUE
            );

            // TODO 왜 AuthJoinException.DuplicateMember 인지? AuthJoinException.DuplicateEmail 이어야하는거 아닌가?
            assertThatThrownBy(() -> memberCommandService.newMember(memberJoinDto))
                    .isExactlyInstanceOf(AuthJoinException.DuplicateMember.class);
        }
    }

    @Nested
    @DisplayName("사용자 프로필 변경 테스트")
    class UpdateMember {

        @Test
        @DisplayName("사용자가 존재하지 않으면 실패한다.")
        void updateMemberTest1() {
            Long DUMMY_MEMBER_ID = 1L;

            assertThatThrownBy(() -> memberCommandService.updateMember(DUMMY_MEMBER_ID, any()))
                    .isExactlyInstanceOf(MemberBusinessException.NotFound.class);
        }

        @Test
        @DisplayName("프로필 변경에 성공한다.")
        void updateMemberTest2() {
            Member REVI = memberRepository.save(REVI());
            MemberUpdateDto UPDATE_DTO = new MemberUpdateDto(
                    KWANGWON_NICKNAME_VALUE,
                    KWANGWON_INTRODUCE_VALUE,
                    KWANGWON_MBTI_VALUE,
                    KWANGWON_KAKAO_LINK,
                    KWANGWON_ADDRESS_VALUE,
                    KWANGWON_ADDRESS_DETAIL_VALUE
            );

            memberCommandService.updateMember(REVI.getId(), UPDATE_DTO);

            assertAll(() -> {
                verify(memberRepository, times(1)).saveAndFlush(REVI);
                assertThat(REVI.getNickname()).isEqualTo(new Nickname(KWANGWON_NICKNAME_VALUE));
                assertThat(REVI.getIntroduce()).isEqualTo(new Introduce(KWANGWON_INTRODUCE_VALUE));
                assertThat(REVI.getMbti()).isSameAs(Mbti.ENFJ);
                assertThat(REVI.getKakaoLink()).isEqualTo(KWANGWON_KAKAO_LINK);
                assertThat(REVI.getAddress())
                        .isEqualTo(new Address(KWANGWON_ADDRESS_VALUE, KWANGWON_ADDRESS_DETAIL_VALUE));
            });
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트")
    class UpdatePassword {

        @Test
        @DisplayName("사용자가 존재하지 않으면 실패한다.")
        void updatePasswordTest1() {
            Long DUMMY_MEMBER_ID = 1L;

            assertThatThrownBy(() -> memberCommandService.updatePassword(DUMMY_MEMBER_ID, any()))
                    .isExactlyInstanceOf(MemberBusinessException.NotFound.class);
        }

        @Test
        @DisplayName("비밀번호 변경에 성공한다.")
        void updatePasswordTest2() {
            Member REVI = memberRepository.save(REVI());
            MemberPasswordUpdateDto UPDATE_DTO = new MemberPasswordUpdateDto(
                    REVI_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE
            );

            memberCommandService.updatePassword(REVI.getId(), UPDATE_DTO);

            assertAll(() -> {
                verify(memberRepository, times(1)).saveAndFlush(REVI);
                assertThat(REVI.getPassword()).isNotEqualTo(Password.encrypted(REVI_ENCRYPTED_PASSWORD_VALUE));
                assertThat(passwordEncoder.matches(ANDONG_PASSWORD_VALUE, REVI.getPassword().getValue())).isTrue();
            });
        }

        @Test
        @DisplayName("현재 비밀번호와 일치하지 않으면 실패한다.")
        void updatePasswordTest3() {
            Member REVI = memberRepository.save(REVI());
            MemberPasswordUpdateDto UPDATE_DTO = new MemberPasswordUpdateDto(
                    KWANGWON_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE,
                    ANDONG_PASSWORD_VALUE
            );

            assertThatThrownBy(() -> memberCommandService.updatePassword(REVI.getId(), UPDATE_DTO))
                    .isExactlyInstanceOf(MemberBusinessException.WrongPassword.class);
        }
    }

    @Nested
    @DisplayName("회원 이미지 변경 테스트")
    class UpdateMemberImage {

        @Test
        @DisplayName("사용자가 존재하지 않으면 실패한다.")
        void updateMemberImageTest1() {
            Long DUMMY_MEMBER_ID = 1L;

            assertThatThrownBy(() -> memberCommandService.updateMemberImage(DUMMY_MEMBER_ID, any()))
                    .isExactlyInstanceOf(MemberBusinessException.NotFound.class);
        }

        @Test
        @DisplayName("사용자가 기본 프로필 이미지를 갖고 있으면, 새로운 이미지로 업데이트한다.")
        void updateMemberImageTest2() {
            Member REVI = memberRepository.save(REVI());
            MockMultipartFile MOCK_FILE = new MockMultipartFile(
                    "file",
                    "test-image.png",
                    "image/png",
                    new byte[]{0x11}
            );
            when(fileStorageManager.upload(any(MultipartFile.class))).thenReturn(CHANGED_PROFILE_IMAGE_LINK);

            memberCommandService.updateMemberImage(REVI.getId(), MOCK_FILE);

            assertAll(() -> {
                verify(fileStorageManager, times(1)).upload(any(MultipartFile.class));
                verify(memberRepository, times(1)).saveAndFlush(any(Member.class));
                assertThat(REVI.getProfileImage()).isEqualTo(CHANGED_PROFILE_IMAGE_LINK);
            });
        }

        @Test
        @DisplayName("사용자가 기본 프로필 이미지를 갖고 있지 않으면, 이미지를 Overwrite 한다.")
        void updateMemberImageTest3() {
            Member REVI = memberRepository.save(REVI());
            REVI.updateProfileImage(CHANGED_PROFILE_IMAGE_LINK);
            MockMultipartFile MOCK_FILE = new MockMultipartFile(
                    "file",
                    "test-image.png",
                    "image/png",
                    new byte[]{0x11}
            );
            when(fileStorageManager.upload(any(MultipartFile.class), anyString()))
                    .thenReturn(CHANGED_PROFILE_IMAGE_LINK);

            memberCommandService.updateMemberImage(REVI.getId(), MOCK_FILE);

            assertAll(() -> {
                verify(fileStorageManager, times(1)).upload(any(MultipartFile.class), anyString());
                verify(memberRepository, times(0)).saveAndFlush(any(Member.class));
                assertThat(REVI.getProfileImage()).isEqualTo(CHANGED_PROFILE_IMAGE_LINK);
            });
        }
    }

    @Nested
    @DisplayName("회원 이미지 삭제 테스트")
    class DeleteMemberImage {

        @Test
        @DisplayName("사용자가 존재하지 않으면 실패한다.")
        void deleteMemberImageTest1() {
            Long DUMMY_MEMBER_ID = 1L;

            assertThatThrownBy(() -> memberCommandService.deleteMemberImage(DUMMY_MEMBER_ID))
                    .isExactlyInstanceOf(MemberBusinessException.NotFound.class);
        }

        @Test
        @DisplayName("사용자가 기본 이미지를 사용하거나 이미지가 없다면, 삭제하지 않는다.")
        void deleteMemberImageTest2() {
            Member REVI = memberRepository.save(REVI());
            REVI.changeDefaultProfileImage();

            memberCommandService.deleteMemberImage(REVI.getId());

            assertThat(events.stream(MemberImageDeleteEvent.class)).hasSize(0);
        }

        @Test
        @DisplayName("사용자가 기본 이미지를 사용하지 않으면 이미지를 삭제한다.")
        void deleteMemberImageTest3() {
            Member REVI = memberRepository.save(REVI());
            REVI.updateProfileImage(CHANGED_PROFILE_IMAGE_LINK);

            memberCommandService.deleteMemberImage(REVI.getId());

            assertAll(() -> {
                assertThat(events.stream(MemberImageDeleteEvent.class)).hasSize(1);
                verify(memberRepository, times(1)).saveAndFlush(any(Member.class));
                assertThat(REVI.getProfileImage()).isEqualTo(PROFILE_IMAGE_LINK);
            });
        }
    }
}