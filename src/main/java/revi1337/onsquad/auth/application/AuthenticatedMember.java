package revi1337.onsquad.auth.application.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import revi1337.onsquad.member.domain.vo.*;
import revi1337.onsquad.member.application.dto.MemberDto;

import java.util.Collection;
import java.util.Set;

public record AuthenticatedMember(
        Long id,
        UserType userType,
        Email email,
        Address address,
        Nickname nickname,
        Password password,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    /**
     * Only Used For @Authenticated HandlerMethodArgumentResolver
     * @param id
     * @return
     */
    public static AuthenticatedMember of(Long id) {
        return new AuthenticatedMember(id, null, null, null, null, null, null);
    }

    // TODO 권한이 도입되면 리팩토링 필요.
    public static AuthenticatedMember of(Long id, UserType userType, Email email, Address address, Nickname nickname, Password password) {
        Set<SimpleGrantedAuthority> roles = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new AuthenticatedMember(
                id,
                userType,
                email,
                address,
                nickname,
                password,
                roles
        );
    }

    public MemberDto toDto() {
        return MemberDto.builder()
                .id(id)
                .userType(userType)
                .email(email)
                .address(address)
                .nickname(nickname)
                .password(password)
                .build();
    }

    public static AuthenticatedMember from(MemberDto memberDto) {
        return AuthenticatedMember.of(
                memberDto.getId(),
                memberDto.getUserType(),
                memberDto.getEmail(),
                memberDto.getAddress(),
                memberDto.getNickname(),
                memberDto.getPassword()
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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
