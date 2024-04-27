package revi1337.onsquad.member.domain;

import jakarta.persistence.*;
import lombok.*;
import revi1337.onsquad.member.domain.vo.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public void updatePassword(CharSequence encodedPassword) {
        this.password = password.update(encodedPassword);
    }
}