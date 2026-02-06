package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.member.application.dto.response.DuplicateResponse;
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
        void test1() {
            Member andong = memberRepository.save(createAndong());

            DuplicateResponse response = memberQueryService.checkDuplicateNickname(andong.getNickname().getValue());

            assertThat(response.duplicate()).isTrue();
        }

        @Test
        void test2() {
            memberRepository.save(createAndong());

            DuplicateResponse response = memberQueryService.checkDuplicateNickname("nick-1");

            assertThat(response.duplicate()).isFalse();
        }
    }

    @Nested
    @DisplayName("이메일 중복 검증")
    class checkDuplicateEmail {

        @Test
        void test1() {
            Member andong = memberRepository.save(createAndong());

            DuplicateResponse response = memberQueryService.checkDuplicateEmail(andong.getEmail().getValue());

            assertThat(response.duplicate()).isTrue();
        }

        @Test
        void test2() {
            memberRepository.save(createAndong());

            DuplicateResponse response = memberQueryService.checkDuplicateEmail("invalid-email@gmail.com");

            assertThat(response.duplicate()).isFalse();
        }
    }

    @Nested
    @DisplayName("회원 조회")
    class findMember {

        @Test
        void test() {
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
