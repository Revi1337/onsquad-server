package revi1337.onsquad.auth.application.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import revi1337.onsquad.auth.application.AuthenticatedMember;

@RequiredArgsConstructor
public class JsonWebTokenAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    private static final String BAD_CREDENTIALS = "invalid user credentials";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailPrincipal = authentication.getPrincipal().toString();
        AuthenticatedMember authenticatedMember = (AuthenticatedMember) userDetailsService.loadUserByUsername(emailPrincipal);
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), authenticatedMember.getPassword())) {
            throw new BadCredentialsException(BAD_CREDENTIALS);
        }

        return UsernamePasswordAuthenticationToken.authenticated(
                authenticatedMember, authenticatedMember.getPassword(), authenticatedMember.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
