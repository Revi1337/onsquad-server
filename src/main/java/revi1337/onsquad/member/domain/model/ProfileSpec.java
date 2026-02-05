package revi1337.onsquad.member.domain.model;

import lombok.Getter;

@Getter
public class ProfileSpec {

    private final String nickname;
    private final String introduce;
    private final String mbti;
    private final String address;
    private final String addressDetail;
    private final String kakaoLink;

    public ProfileSpec(String nickname, String introduce, String mbti, String address, String addressDetail, String kakaoLink) {
        this.nickname = nickname;
        this.introduce = introduce;
        this.mbti = mbti;
        this.address = address;
        this.addressDetail = addressDetail;
        this.kakaoLink = kakaoLink;
    }
}
