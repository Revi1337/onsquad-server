package revi1337.onsquad.member.dto;

import lombok.Builder;
import lombok.Getter;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.*;

@Getter
public class MemberDto {

    private Long id;
    private UserType userType;
    private Email email;
    private Address address;
    private Nickname nickname;
    private Password password;

    @Builder(builderClassName = "MemberDtoBuilder")
    private MemberDto(Long id, UserType userType, Email email, Address address, Nickname nickname, Password password) {
        this.id = id;
        this.userType = userType;
        this.email = email;
        this.address = address;
        this.nickname = nickname;
        this.password = password;
    }

    @Builder(builderClassName = "MemberDtoValueBuilder", builderMethodName = "create")
    private MemberDto(Long id, String userType, String email, String address, String nickname, String password) {
        this.id = id;
        this.userType = userType == null ? UserType.GENERAL : UserType.valueOf(userType);
        this.email = new Email(email);
        this.address = new Address(address);
        this.nickname = new Nickname(nickname);
        this.password = new Password(password);
    }

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .userType(member.getUserType())
                .email(member.getEmail())
                .address(member.getAddress())
                .nickname(member.getNickname())
                .password(member.getPassword())
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .id(id)
                .userType(userType)
                .email(email)
                .address(address)
                .nickname(nickname)
                .password(password)
                .build();
    }
}
