package revi1337.onsquad.member.application;

import static revi1337.onsquad.auth.error.AuthErrorCode.DUPLICATE_MEMBER;
import static revi1337.onsquad.auth.error.AuthErrorCode.NON_AUTHENTICATE_EMAIL;
import static revi1337.onsquad.member.error.MemberErrorCode.WRONG_PASSWORD;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.error.exception.AuthJoinException;
import revi1337.onsquad.inrastructure.mail.application.AuthMailManager;
import revi1337.onsquad.member.application.dto.MemberInfoDto;
import revi1337.onsquad.member.application.dto.MemberJoinDto;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;
import revi1337.onsquad.member.application.event.MemberUpdateEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthMailManager authMailManager;

    public boolean checkDuplicateNickname(String nickname) {
        return memberRepository.existsByNickname(new Nickname(nickname));
    }

    @Transactional
    public void newMember(MemberJoinDto dto) {
        verifyAttribute(dto);
        Member member = dto.toEntity();
        member.updatePassword(passwordEncoder.encode(dto.password()));
        memberRepository.save(member);
    }

    public MemberInfoDto findMember(Long memberId) {
        Member member = memberRepository.getById(memberId);

        return MemberInfoDto.from(member);
    }

    @Transactional
    public void updatePassword(Long memberId, MemberPasswordUpdateDto dto) {
        Member member = memberRepository.getById(memberId);
        if (!passwordEncoder.matches(dto.currentPassword(), member.getPassword().getValue())) {
            throw new MemberBusinessException.WrongPassword(WRONG_PASSWORD, member.getId());
        }

        member.updatePassword(passwordEncoder.encode(dto.newPassword()));
        memberRepository.saveAndFlush(member);
    }

    @Transactional
    public void updateProfile(Long memberId, MemberUpdateDto dto, MultipartFile file) {
        Member member = memberRepository.getById(memberId);
        member.updateProfile(dto.toEntity());
        memberRepository.saveAndFlush(member);
        publishEventIfMultipartAvailable(file, member);
    }

    private void verifyAttribute(MemberJoinDto dto) {
        if (!authMailManager.isValidMailStatus(dto.email())) {
            throw new AuthJoinException.NonAuthenticateEmail(NON_AUTHENTICATE_EMAIL);
        }
        if (memberRepository.existsByEmail(new Email(dto.email()))) {
            throw new AuthJoinException.DuplicateMember(DUPLICATE_MEMBER);
        }
    }

    private void publishEventIfMultipartAvailable(MultipartFile file, Member member) {
        if (file == null || file.isEmpty()) {
            return;
        }
        try {
            applicationEventPublisher.publishEvent(
                    new MemberUpdateEvent(member.getId(), file.getBytes(), file.getOriginalFilename())
            );
        } catch (IOException ignored) {
        }
    }
}
