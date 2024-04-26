package revi1337.onsquad.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.member.domain.vo.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Embedded
    private Email email;

    @Embedded
    private Address address;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Password password;

    @Builder
    private Member(UserType userType, Email email, Address address, Nickname nickname, Password password) {
        this.userType = userType;
        this.email = email;
        this.address = address;
        this.nickname = nickname;
        this.password = password;
    }

    public void changePassword(Password encodedPassword) {
        this.password = encodedPassword;
    }

}