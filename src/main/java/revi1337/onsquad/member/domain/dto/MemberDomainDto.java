package revi1337.onsquad.member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.entity.vo.Address;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Introduce;
import revi1337.onsquad.member.domain.entity.vo.Mbti;
import revi1337.onsquad.member.domain.entity.vo.Nickname;
import revi1337.onsquad.member.domain.entity.vo.UserType;

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
