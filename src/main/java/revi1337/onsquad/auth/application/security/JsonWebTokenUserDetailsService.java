package revi1337.onsquad.auth.application.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import revi1337.onsquad.auth.application.AuthMemberAttribute;
import revi1337.onsquad.member.application.dto.MemberDto;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;

@RequiredArgsConstructor
public class JsonWebTokenUserDetailsService implements UserDetailsService {

    private static final String MESSAGE = "username not found";

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(new Email(email))
                .map(MemberDto::from)
                .map(AuthMemberAttribute::from)
                .orElseThrow(() -> new UsernameNotFoundException(MESSAGE));
    }
}
