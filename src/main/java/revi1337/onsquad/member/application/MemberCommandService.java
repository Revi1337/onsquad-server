package revi1337.onsquad.member.application;

import static revi1337.onsquad.auth.error.AuthErrorCode.DUPLICATE_MEMBER;
import static revi1337.onsquad.auth.error.AuthErrorCode.DUPLICATE_NICKNAME;
import static revi1337.onsquad.auth.error.AuthErrorCode.NON_AUTHENTICATE_EMAIL;
import static revi1337.onsquad.member.error.MemberErrorCode.WRONG_PASSWORD;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.auth.error.exception.AuthJoinException;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepository;
import revi1337.onsquad.member.application.dto.MemberCreateDto;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;
import revi1337.onsquad.member.application.event.MemberImageDeleteEvent;
import revi1337.onsquad.member.application.event.MemberImageUpdateEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final VerificationCodeRepository repositoryChain;

    public void newMember(MemberCreateDto dto) {
        ensureRequirements(dto);
        Member member = dto.toEntity();
        member.updatePassword(passwordEncoder.encode(dto.password()));
        memberRepository.save(member);
    }

    @Transactional
    public void updateMember(Long memberId, MemberUpdateDto dto) {
        Member member = memberRepository.getById(memberId);
        member.updateProfile(dto.toMemberBase());
        memberRepository.saveAndFlush(member);
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

    public void updateImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.getById(memberId);
        applicationEventPublisher.publishEvent(new MemberImageUpdateEvent(member, file));
    }

    public void deleteImage(Long memberId) {
        Member member = memberRepository.getById(memberId);
        if (member.hasNotDefaultImage()) {
            applicationEventPublisher.publishEvent(new MemberImageDeleteEvent(member));
        }
    }

    private void ensureRequirements(MemberCreateDto dto) {
        if (!repositoryChain.isMarkedVerificationStatusWith(dto.email(), VerificationStatus.SUCCESS)) {
            throw new AuthJoinException.NonAuthenticateEmail(NON_AUTHENTICATE_EMAIL);
        }
        if (memberRepository.existsByNickname(new Nickname(dto.nickname()))) {
            throw new AuthJoinException.DuplicateNickname(DUPLICATE_NICKNAME, dto.nickname());
        }
        if (memberRepository.existsByEmail(new Email(dto.email()))) {
            throw new AuthJoinException.DuplicateMember(DUPLICATE_MEMBER);
        }
    }
}
