package revi1337.onsquad.factory;

import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.*;

public class MemberFactory {

    private static final Nickname NICKNAME = new Nickname("nickname");
    private static final Email EMAIL = new Email("test@email.com");
    private static final Password PASSWORD = new Password("12345!@asa");
    private static final Address ADDRESS = new Address("어딘가", "롯데캐슬");
    private static final UserType USER_TYPE = UserType.GENERAL;

    public static Member withNickname(Nickname nickname) {
        return defaultMember().nickname(nickname).build();
    }

    public static Member withEmail(Email email) {
        return defaultMember().email(email).build();
    }

    public static Member withPassword(Password password) {
        return defaultMember().password(password).build();
    }

    public static Member withAddress(Address address) {
        return defaultMember().address(address).build();
    }

    public static Member withUserType(UserType userType) {
        return defaultMember().userType(userType).build();
    }

    public static Member.MemberBuilder defaultMember() {
        return Member.builder()
                .nickname(NICKNAME)
                .address(ADDRESS)
                .email(EMAIL)
                .password(PASSWORD)
                .userType(USER_TYPE);
    }
}
