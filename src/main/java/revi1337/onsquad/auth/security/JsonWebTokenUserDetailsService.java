package revi1337.onsquad.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import revi1337.onsquad.auth.support.CurrentMember;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.repository.MemberRepository;

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
