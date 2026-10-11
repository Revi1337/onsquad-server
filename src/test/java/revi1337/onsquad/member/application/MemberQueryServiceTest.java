package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.dto.DuplicateResponse;
import revi1337.onsquad.member.application.dto.response.MemberResponse;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

class MemberQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberQueryService memberQueryService;

    @Nested
    @DisplayName("닉네임 중복 검증")
    class checkDuplicateNickname {

        @Test
        @DisplayName("이미 사용 중인 닉네임이면 중복으로 응답한다.")
        void returnsDuplicate() {
            Member andong = memberRepository.save(createAndong());

            DuplicateResponse response = memberQueryService.checkDuplicateNickname(andong.getNickname().getValue());

            assertThat(response.duplicate()).isTrue();
        }

        @Test
        @DisplayName("사용 중이지 않은 닉네임이면 중복이 아니라고 응답한다.")
        void returnsNotDuplicate() {
            memberRepository.save(createAndong());

            DuplicateResponse response = memberQueryService.checkDuplicateNickname("nick-1");

            assertThat(response.duplicate()).isFalse();
        }
    }

    @Nested
    @DisplayName("이메일 중복 검증")
    class checkDuplicateEmail {

        @Test
        @DisplayName("이미 사용 중인 이메일이면 중복으로 응답한다.")
        void returnsDuplicate() {
            Member andong = memberRepository.save(createAndong());

            DuplicateResponse response = memberQueryService.checkDuplicateEmail(andong.getEmail().getValue());

            assertThat(response.duplicate()).isTrue();
        }

        @Test
        @DisplayName("사용 중이지 않은 이메일이면 중복이 아니라고 응답한다.")
        void returnsNotDuplicate() {
            memberRepository.save(createAndong());

            DuplicateResponse response = memberQueryService.checkDuplicateEmail("invalid-email@gmail.com");

            assertThat(response.duplicate()).isFalse();
        }
    }

    @Nested
    @DisplayName("회원 조회")
    class findMember {

        @Test
        @DisplayName("회원 ID 로 회원 정보를 조회한다.")
        void returnsMember() {
            Member andong = memberRepository.save(createAndong());
            clearPersistenceContext();

            MemberResponse response = memberQueryService.findMember(andong.getId());

            assertSoftly(softly -> {
                softly.assertThat(response.id()).isEqualTo(andong.getId());
                softly.assertThat(response.email()).isEqualTo(andong.getEmail().getValue());
                softly.assertThat(response.nickname()).isEqualTo(andong.getNickname().getValue());
                softly.assertThat(response.mbti()).isSameAs(andong.getMbti().name());
                softly.assertThat(response.kakaoLink()).isEqualTo(andong.getKakaoLink());
                softly.assertThat(response.profileImage()).isEqualTo(andong.getProfileImage());
                softly.assertThat(response.userType()).isEqualTo(andong.getUserType().getText());
                softly.assertThat(response.address()).isEqualTo(andong.getAddress().getValue());
                softly.assertThat(response.addressDetail()).isEqualTo(andong.getAddress().getDetail());
            });
        }
    }
}
