package revi1337.onsquad.member.application.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.inrastructure.file.support.RecycleBin;
import revi1337.onsquad.member.application.event.MemberImageDeleteEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberImageDeleteEventListener {

    private final MemberRepository memberRepository;

    @EventListener(MemberImageDeleteEvent.class)
    public void handleMemberImageDeleteEvent(MemberImageDeleteEvent event) {
        Member member = event.member();
        RecycleBin.append(member.getProfileImage());
        member.changeDefaultProfileImage();
        memberRepository.saveAndFlush(member);
    }
}
