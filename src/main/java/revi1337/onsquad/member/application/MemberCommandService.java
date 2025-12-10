package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.member.application.dto.MemberCreateDto;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Nickname;
import revi1337.onsquad.member.domain.event.MemberImageDeleteEvent;
import revi1337.onsquad.member.domain.event.MemberImageUpdateEvent;
import revi1337.onsquad.member.domain.model.VerificationStatus;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.member.domain.repository.VerificationCodeRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

@RequiredArgsConstructor
@Service
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final VerificationCodeRepository redisCodeRepository;

    public void newMember(MemberCreateDto dto) {
        ensureRequirements(dto);
        Member member = dto.toEntity();
        member.updatePassword(passwordEncoder.encode(dto.password()));
        memberRepository.save(member);
    }

    @Transactional
    public void updateMember(Long memberId, MemberUpdateDto dto) {
        Member member = validateMemberExistsAndGet(memberId);
        member.updateProfile(dto.toMemberBase());
    }

    @Transactional
    public void updatePassword(Long memberId, MemberPasswordUpdateDto dto) {
        Member member = validateMemberExistsAndGet(memberId);
        if (!passwordEncoder.matches(dto.currentPassword(), member.getPassword().getValue())) {
            throw new MemberBusinessException.WrongPassword(MemberErrorCode.WRONG_PASSWORD);
        }

        member.updatePassword(passwordEncoder.encode(dto.newPassword()));
    }

    public void updateImage(Long memberId, MultipartFile file) {
        Member member = validateMemberExistsAndGet(memberId);
        applicationEventPublisher.publishEvent(new MemberImageUpdateEvent(member, file));
    }

    public void deleteImage(Long memberId) {
        Member member = validateMemberExistsAndGet(memberId);
        if (member.hasNotDefaultImage()) {
            applicationEventPublisher.publishEvent(new MemberImageDeleteEvent(member));
        }
    }

    private void ensureRequirements(MemberCreateDto dto) { // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에
        if (!redisCodeRepository.isMarkedVerificationStatusWith(dto.email(), VerificationStatus.SUCCESS)) {
            throw new MemberBusinessException.NonAuthenticateEmail(MemberErrorCode.NON_AUTHENTICATE_EMAIL);
        }
        if (memberRepository.existsByNickname(new Nickname(dto.nickname()))) {
            throw new MemberBusinessException.DuplicateNickname(MemberErrorCode.DUPLICATE_NICKNAME);
        }
        if (memberRepository.existsByEmail(new Email(dto.email()))) {
            throw new MemberBusinessException.DuplicateEmail(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    private Member validateMemberExistsAndGet(Long memberId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOT_FOUND));
    }
}
