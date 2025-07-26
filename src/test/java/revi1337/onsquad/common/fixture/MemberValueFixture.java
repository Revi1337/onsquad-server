package revi1337.onsquad.common.fixture;

import java.util.UUID;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Introduce;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.member.domain.vo.UserType;

public class MemberValueFixture {

    public static final String EMAIL_VALUE = "email@gmail.com";
    public static final String REVI_EMAIL_VALUE = "david122123@gmail.com";
    public static final String ANDONG_EMAIL_VALUE = "ax34554@gmail.com";
    public static final String KWANGWON_EMAIL_VALUE = "kakd1313@gmail.com";
    public static final String DUMMY_EMAIL_VALUE_1 = "dummy1@gmail.com";
    public static final String DUMMY_EMAIL_VALUE_2 = "dummy2@gmail.com";
    public static final String DUMMY_EMAIL_VALUE_3 = "dummy3@gmail.com";
    public static final String DUMMY_EMAIL_VALUE_4 = "dummy4@gmail.com";
    public static final String DUMMY_EMAIL_VALUE_5 = "dummy5@gmail.com";
    public static final String DUMMY_EMAIL_VALUE_6 = "dummy6@gmail.com";
    public static final String DUMMY_EMAIL_VALUE_7 = "dummy7@gmail.com";

    public static final String NICKNAME_VALUE = "nickname";
    public static final String REVI_NICKNAME_VALUE = "revi1337";
    public static final String ANDONG_NICKNAME_VALUE = "andong";
    public static final String KWANGWON_NICKNAME_VALUE = "kwon";

    public static final String ADDRESS_VALUE = "공백";
    public static final String REVI_ADDRESS_VALUE = "REVI 주소";
    public static final String ANDONG_ADDRESS_VALUE = "ANDONG 주소";
    public static final String KWANGWON_ADDRESS_VALUE = "KWANGWON 주소";

    public static final String ADDRESS_DETAIL_VALUE = "공백";
    public static final String REVI_ADDRESS_DETAIL_VALUE = "REVI 상세 주소";
    public static final String ANDONG_ADDRESS_DETAIL_VALUE = "ANDONG 상세 주소";
    public static final String KWANGWON_ADDRESS_DETAIL_VALUE = "KWANGWON 상세 주소";

    public static final String PASSWORD_VALUE = "12345!@asa";
    public static final String REVI_PASSWORD_VALUE = "12345!@asa";
    public static final String REVI_UUID_PASSWORD_VALUE = UUID.randomUUID().toString();
    public static final String ANDONG_PASSWORD_VALUE = "12345!@asb";
    public static final String KWANGWON_PASSWORD_VALUE = "12345!@asc";

    public static final String ENCRYPTED_PASSWORD_VALUE = "{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.";
    public static final String REVI_ENCRYPTED_PASSWORD_VALUE = "{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.";
    public static final String ANDONG_ENCRYPTED_PASSWORD_VALUE = "{bcrypt}$2a$10$ocuCgB1yFSe34jsD20MC4eWiXIoUqtUaRpw2DaAbLMrJpDGp2fSYW";
    public static final String KWANGWON_ENCRYPTED_PASSWORD_VALUE = "{bcrypt}$2a$10$NaBOLRjMFC0BHl7AnnXrV.srIYwV9uTwiStOHdr15P4mR6vEVp2Z2";

    public static final String INTRODUCE_VALUE = "소개 없음";
    public static final String REVI_INTRODUCE_VALUE = "Hello World REVI";
    public static final String ANDONG_INTRODUCE_VALUE = "Hello World ANDONG";
    public static final String KWANGWON_INTRODUCE_VALUE = "Hello World KWANGWON";

    public static final String PROFILE_IMAGE_LINK = "https://d3jao8gvkosd1k.cloudfront.net/onsquad/default/member-default.svg";
    public static final String CHANGED_PROFILE_IMAGE_LINK = "https://changeed_img.com";
    public static final String REVI_PROFILE_IMAGE_LINK = "https://d3jao8gvkosd1k.cloudfront.net/onsquad/default/member-default.svg";
    public static final String REVI_OAUTH_PROFILE_IMAGE_LINK = "https:///oauth2/onsquad/default/member-default.svg";
    public static final String ANDONG_PROFILE_IMAGE_LINK = "https://d3jao8gvkosd1k.cloudfront.net/onsquad/default/member-default.svg";
    public static final String KWANGWON_PROFILE_IMAGE_LINK = "https://d3jao8gvkosd1k.cloudfront.net/onsquad/default/member-default.svg";

    public static final String KAKAO_LINK = "";
    public static final String REVI_KAKAO_LINK = "https://revi.kakao.com";
    public static final String ANDONG_KAKAO_LINK = "https://andong.kakao.com";
    public static final String KWANGWON_KAKAO_LINK = "https://kwangwon.kakao.com";

    public static final String REVI_MBTI_VALUE = Mbti.ISTP.name();
    public static final String ANDONG_MBTI_VALUE = Mbti.ISFP.name();
    public static final String KWANGWON_MBTI_VALUE = Mbti.ENFJ.name();

    public static final String REVI_USER_TYPE_VALUE = UserType.GENERAL.getText();
    public static final String ANDONG_USER_TYPE_VALUE = UserType.GENERAL.getText();
    public static final String KWANGWON_USER_TYPE_VALUE = UserType.GENERAL.getText();

    public static final String VALID_AUTHENTICATION_CODE = "VALID_CODE";
    public static final String INVALID_AUTHENTICATION_CODE = "INVALID_CODE";

    public static final Email EMAIL = new Email("email@gmail.com");
    public static final Email REVI_EMAIL = new Email("david122123@gmail.com");
    public static final Email ANDONG_EMAIL = new Email("ax34554@gmail.com");
    public static final Email KWANGWON_EMAIL = new Email("kakd1313@gmail.com");

    public static final Nickname NICKNAME = new Nickname("nickname");
    public static final Nickname REVI_NICKNAME = new Nickname("revi1337");
    public static final Nickname ANDONG_NICKNAME = new Nickname("andong");
    public static final Nickname KWANGWON_NICKNAME = new Nickname("kwon");

    public static final Address ADDRESS = new Address("공백", "공백");
    public static final Address REVI_ADDRESS = new Address("REVI 주소", "REVI 상세 주소");
    public static final Address ANDONG_ADDRESS = new Address("ANDONG 주소", "ANDONG 상세 주소");
    public static final Address KWANGWON_ADDRESS = new Address("KWANGWON 주소", "KWANGWON 상세 주소");

    public static final Password PASSWORD = Password.raw("12345!@asa");
    public static final Password REVI_PASSWORD = Password.raw("12345!@asa");
    public static final Password ANDONG_PASSWORD = Password.raw("12345!@asb");
    public static final Password KWANGWON_PASSWORD = Password.raw("12345!@asc");
    public static final Password ENCRYPTED_PASSWORD = Password.encrypted("{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.");
    public static final Password REVI_ENCRYPTED_PASSWORD = Password.encrypted("{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.");
    public static final Password ANDONG_ENCRYPTED_PASSWORD = Password.encrypted("{bcrypt}$2a$10$ocuCgB1yFSe34jsD20MC4eWiXIoUqtUaRpw2DaAbLMrJpDGp2fSYW");
    public static final Password KWANGWON_ENCRYPTED_PASSWORD = Password.encrypted("{bcrypt}$2a$10$NaBOLRjMFC0BHl7AnnXrV.srIYwV9uTwiStOHdr15P4mR6vEVp2Z2");

    public static final Introduce INTRODUCE = new Introduce("소개 없음");
    public static final Introduce REVI_INTRODUCE = new Introduce("Hello World REVI");
    public static final Introduce ANDONG_INTRODUCE = new Introduce("Hello World ANDONG");
    public static final Introduce KWANGWON_INTRODUCE = new Introduce("Hello World KWANGWON");

    public static final Mbti REVI_MBTI = Mbti.ISTP;
    public static final Mbti ANDONG_MBTI = Mbti.ISFP;
    public static final Mbti KWANGWON_MBTI = Mbti.ENFJ;

    public static final UserType REVI_USER_TYPE = UserType.GENERAL;
    public static final UserType ANDONG_USER_TYPE = UserType.GENERAL;
    public static final UserType KWANGWON_USER_TYPE = UserType.GENERAL;

}
