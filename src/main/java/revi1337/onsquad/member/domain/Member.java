package revi1337.onsquad.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import revi1337.onsquad.member.domain.vo.*;

import java.util.Objects;

@ToString
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ColumnDefault("'GENERAL'")
    @Column(nullable = false)
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
    private Member(Long id, UserType userType, Email email, Address address, Nickname nickname, Password password) {
        this.id = id;
        this.userType = userType == null ? UserType.GENERAL : userType;
        this.email = email;
        this.address = address;
        this.nickname = nickname;
        this.password = password;
    }

    public void updatePassword(CharSequence encodedPassword) {
        this.password = password.update(encodedPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;
        return id != null && Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}