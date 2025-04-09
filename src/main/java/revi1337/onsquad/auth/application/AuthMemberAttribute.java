package revi1337.onsquad.auth.application;

import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import revi1337.onsquad.member.application.dto.MemberDto;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.member.domain.vo.UserType;

// TODO 이거 패키지를 auth > model 로 옮겨야한다.
public record AuthMemberAttribute(
        Long id,
        Email email,
        Password password,
        UserType userType,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    /**
     * Only Used For @Authenticated HandlerMethodArgumentResolver
     *
     * @param id
     * @return
     */
    public static AuthMemberAttribute of(Long id) {
        return new AuthMemberAttribute(id, null, null, null, null);
    }

    public static AuthMemberAttribute of(Long id, Email email, Password password, UserType userType) {
        Set<SimpleGrantedAuthority> roles = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new AuthMemberAttribute(
                id,
                email,
                password,
                userType,
                roles
        );
    }

    public MemberDto toDto() {
        return MemberDto.builder()
                .id(id)
                .email(email)
                .password(password)
                .userType(userType)
                .build();
    }

    public static AuthMemberAttribute from(MemberDto memberDto) {
        return AuthMemberAttribute.of(
                memberDto.getId(),
                memberDto.getEmail(),
                memberDto.getPassword(),
                memberDto.getUserType()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password.getValue();
    }

    @Override
    public String getUsername() {
        return email.getValue();
    }
}
