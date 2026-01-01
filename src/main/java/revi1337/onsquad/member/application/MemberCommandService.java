package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.application.dto.MemberCreateDto;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;
import revi1337.onsquad.member.application.dto.MemberUpdateDto;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberCommandService {

    private final MemberAccessor memberAccessor;
    private final MemberCreateService memberCreateService;
    private final MemberPasswordUpdateService memberPasswordUpdateService;
    private final MemberRepository memberRepository;

    public void newMember(MemberCreateDto dto) {
        Member member = memberCreateService.attemptCreate(dto);
        memberRepository.save(member);
    }

    public void updateMember(Long memberId, MemberUpdateDto dto) {
        Member member = memberAccessor.getById(memberId);
        member.updateProfile(dto.toMemberBase());
        memberRepository.saveAndFlush(member);
    }

    public void updatePassword(Long memberId, MemberPasswordUpdateDto dto) {
        Member member = memberPasswordUpdateService.attemptUpdate(memberId, dto);
        memberRepository.saveAndFlush(member);
    }
}
