package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;
import revi1337.onsquad.member.application.event.MemberUpdateEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.application.dto.MemberInfoDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public MemberInfoDto findMember(Long memberId) {
        Member member = memberRepository.getById(memberId);

        return MemberInfoDto.from(member);
    }

    @Transactional
    public void updateProfile(Long memberId, MemberUpdateDto dto, MultipartFile file) {
        Member member = memberRepository.getById(memberId);
        member.updateProfile(dto.toEntity());
        memberRepository.saveAndFlush(member);
        if (file != null && !file.isEmpty()) {
            applicationEventPublisher.publishEvent(new MemberUpdateEvent(member, file));
        }
    }
}
