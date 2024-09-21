package revi1337.onsquad.auth.application.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import revi1337.onsquad.auth.application.AuthenticatedMember;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.application.dto.MemberDto;

@RequiredArgsConstructor
public class JsonWebTokenUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private static final String MESSAGE = "username not found";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(new Email(email))
                .map(MemberDto::from)
                .map(AuthenticatedMember::from)
                .orElseThrow(() -> new UsernameNotFoundException(MESSAGE));
    }
}
