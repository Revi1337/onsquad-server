package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.member.application.dto.MemberCreateDto;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.event.MemberImageDeleteEvent;
import revi1337.onsquad.member.domain.event.MemberImageUpdateEvent;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class MemberCommandService {

    private final MemberAccessPolicy memberAccessPolicy;
    private final MemberCreateService memberCreateService;
    private final MemberPasswordUpdateService memberPasswordUpdateService;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void newMember(MemberCreateDto dto) {
        Member member = memberCreateService.attemptCreate(dto);
        memberRepository.save(member);
    }

    @Transactional
    public void updateMember(Long memberId, MemberUpdateDto dto) {
        Member member = memberAccessPolicy.ensureMemberExistsAndGet(memberId);
        member.updateProfile(dto.toMemberBase());
        memberRepository.saveAndFlush(member);
    }

    @Transactional
    public void updatePassword(Long memberId, MemberPasswordUpdateDto dto) {
        Member member = memberPasswordUpdateService.attemptUpdate(memberId, dto);
        memberRepository.saveAndFlush(member);
    }

    public void updateImage(Long memberId, MultipartFile file) {
        Member member = memberAccessPolicy.ensureMemberExistsAndGet(memberId);
        applicationEventPublisher.publishEvent(new MemberImageUpdateEvent(member, file));
    }

    public void deleteImage(Long memberId) {
        Member member = memberAccessPolicy.ensureMemberExistsAndGet(memberId);
        if (member.hasNotDefaultImage()) {
            applicationEventPublisher.publishEvent(new MemberImageDeleteEvent(member));
        }
    }
}
