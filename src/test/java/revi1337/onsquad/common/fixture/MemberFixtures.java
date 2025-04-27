package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_ENCRYPTED_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ENCRYPTED_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_ENCRYPTED_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ENCRYPTED_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PROFILE_IMAGE_LINK;

import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Introduce;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.member.domain.vo.UserType;

public class MemberFixtures {

    public static Member DEFAULT() {
        return Member.builder()
                .email(new Email(EMAIL_VALUE))
                .nickname(new Nickname(NICKNAME_VALUE))
                .password(Password.encrypted(ENCRYPTED_PASSWORD_VALUE))
                .kakaoLink(KAKAO_LINK)
                .mbti(Mbti.ENTP)
                .build();
    }

    public static Member REVI() {
        return Member.builder()
                .email(new Email(REVI_EMAIL_VALUE))
                .nickname(new Nickname(REVI_NICKNAME_VALUE))
                .introduce(new Introduce(REVI_INTRODUCE_VALUE))
                .address(new Address(REVI_ADDRESS_VALUE, REVI_ADDRESS_DETAIL_VALUE))
                .password(Password.encrypted(REVI_ENCRYPTED_PASSWORD_VALUE))
                .profileImage(REVI_PROFILE_IMAGE_LINK)
                .kakaoLink(REVI_KAKAO_LINK)
                .mbti(Mbti.ISTP)
                .build();
    }

    public static Member ANDONG() {
        return Member.builder()
                .email(new Email(ANDONG_EMAIL_VALUE))
                .nickname(new Nickname(ANDONG_NICKNAME_VALUE))
                .introduce(new Introduce(ANDONG_INTRODUCE_VALUE))
                .address(new Address(ANDONG_ADDRESS_VALUE, ANDONG_ADDRESS_DETAIL_VALUE))
                .password(Password.encrypted(ANDONG_ENCRYPTED_PASSWORD_VALUE))
                .profileImage(ANDONG_PROFILE_IMAGE_LINK)
                .kakaoLink(ANDONG_KAKAO_LINK)
                .mbti(Mbti.ISFP)
                .build();
    }

    public static Member KWANGWON() {
        return Member.builder()
                .email(new Email(KWANGWON_EMAIL_VALUE))
                .nickname(new Nickname(KWANGWON_NICKNAME_VALUE))
                .introduce(new Introduce(KWANGWON_INTRODUCE_VALUE))
                .address(new Address(KWANGWON_ADDRESS_VALUE, KWANGWON_ADDRESS_DETAIL_VALUE))
                .password(Password.encrypted(KWANGWON_ENCRYPTED_PASSWORD_VALUE))
                .profileImage(KWANGWON_PROFILE_IMAGE_LINK)
                .kakaoLink(KWANGWON_KAKAO_LINK)
                .mbti(Mbti.ISTP)
                .build();
    }

    public static Member REVI_GOOGLE() {
        return Member.builder()
                .email(new Email(REVI_EMAIL_VALUE))
                .nickname(new Nickname(REVI_NICKNAME_VALUE))
                .introduce(new Introduce(REVI_INTRODUCE_VALUE))
                .password(Password.encrypted(REVI_ENCRYPTED_PASSWORD_VALUE))
                .kakaoLink(REVI_KAKAO_LINK)
                .mbti(Mbti.ISTP)
                .userType(UserType.GOOGLE)
                .build();
    }

    public static Member REVI_WITH_ID(Long id) {
        Member revi = REVI();
        ReflectionTestUtils.setField(revi, "id", id);
        return revi;
    }

    public static Member ANDONG_WITH_ID(Long id) {
        Member andong = ANDONG();
        ReflectionTestUtils.setField(andong, "id", id);
        return andong;
    }

    public static Member KWANGWON_WITH_ID(Long id) {
        Member kwangwon = KWANGWON();
        ReflectionTestUtils.setField(kwangwon, "id", id);
        return kwangwon;
    }
}