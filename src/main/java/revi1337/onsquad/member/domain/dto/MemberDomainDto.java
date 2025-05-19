package revi1337.onsquad.member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Introduce;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.UserType;

public record MemberDomainDto(
        Long id,
        Email email,
        Nickname nickname,
        Introduce introduce,
        Mbti mbti,
        String kakaoLink,
        String profileImage,
        UserType userType,
        Address address
) {
    @QueryProjection
    public MemberDomainDto(
            Long id,
            Email email,
            Nickname nickname,
            Introduce introduce,
            Mbti mbti,
            String kakaoLink,
            String profileImage,
            UserType userType,
            Address address
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.introduce = introduce;
        this.mbti = mbti;
        this.kakaoLink = kakaoLink;
        this.profileImage = profileImage;
        this.userType = userType;
        this.address = address;
    }
}
