package revi1337.onsquad.auth.application.token.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import revi1337.onsquad.auth.application.AuthMemberAttribute;

@RequiredArgsConstructor
public class JsonWebTokenAuthenticationProvider implements AuthenticationProvider {

    private static final String BAD_CREDENTIALS = "invalid user credentials";

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailPrincipal = authentication.getPrincipal().toString();
        AuthMemberAttribute authMemberAttribute = (AuthMemberAttribute) userDetailsService
                .loadUserByUsername(emailPrincipal);
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), authMemberAttribute.getPassword())) {
            throw new BadCredentialsException(BAD_CREDENTIALS);
        }

        return UsernamePasswordAuthenticationToken.authenticated(
                authMemberAttribute, authMemberAttribute.getPassword(), authMemberAttribute.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
