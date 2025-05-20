package revi1337.onsquad.auth.application.token.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import revi1337.onsquad.auth.application.CurrentMember;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;

@RequiredArgsConstructor
public class JsonWebTokenUserDetailsService implements UserDetailsService {

    private static final String MESSAGE = "사용자를 찾을 수 없습니다.";

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(new Email(email))
                .map(MemberSummary::from)
                .map(CurrentMember::from)
                .orElseThrow(() -> new UsernameNotFoundException(MESSAGE));
    }
}
