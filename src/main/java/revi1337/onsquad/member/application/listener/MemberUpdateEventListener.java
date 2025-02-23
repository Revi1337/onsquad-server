package revi1337.onsquad.member.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.inrastructure.s3.application.S3StorageManager;
import revi1337.onsquad.member.application.event.MemberUpdateEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberUpdateEventListener {

    private final MemberRepository memberRepository;
    private final S3StorageManager memberS3StorageManager;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleMemberUpdateEvent(MemberUpdateEvent event) {
        try {
            Member member = memberRepository.getById(event.memberId());
            String uploadUrl = memberS3StorageManager.updateFile(
                    member.getProfileImage(), event.fileContent(), event.originalFilename()
            );
            member.updateProfileImage(uploadUrl);
            log.debug("[사용자 프로필 이미지 업데이트 성공] : member_id = {}", event.memberId());
        } catch (Exception e) {
            log.error("[사용자 프로필 이미지 업데이트 실패] : member_id = {}", event.memberId());
        }
    }
}
