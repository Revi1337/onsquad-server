package revi1337.onsquad.member.application.dto;

import lombok.Builder;
import lombok.Getter;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.UserType;

@Getter
public class MemberDto {

    private Long id;
    private UserType userType;
    private Email email;
    private Address address;
    private Nickname nickname;

    @Builder
    private MemberDto(Long id, UserType userType, Email email, Address address, Nickname nickname) {
        this.id = id;
        this.userType = userType;
        this.email = email;
        this.address = address;
        this.nickname = nickname;
    }

    public Member toEntity() {
        return Member.builder()
                .userType(userType)
                .email(email)
                .address(address)
                .nickname(nickname)
                .build();
    }
}
