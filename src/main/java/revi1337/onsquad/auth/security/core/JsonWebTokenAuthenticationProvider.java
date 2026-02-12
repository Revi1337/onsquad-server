package revi1337.onsquad.auth.security.core;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import revi1337.onsquad.auth.support.CurrentMember;

@RequiredArgsConstructor
public class JsonWebTokenAuthenticationProvider implements AuthenticationProvider {

    private static final String BAD_CREDENTIALS = "비밀번호가 일치하지 않습니다.";

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailPrincipal = authentication.getPrincipal().toString();
        CurrentMember currentMember = (CurrentMember) userDetailsService.loadUserByUsername(emailPrincipal);
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), currentMember.getPassword())) {
            throw new BadCredentialsException(BAD_CREDENTIALS);
        }

        return UsernamePasswordAuthenticationToken.authenticated(
                currentMember, currentMember.getPassword(), currentMember.getAuthorities()
        );
    }
}
