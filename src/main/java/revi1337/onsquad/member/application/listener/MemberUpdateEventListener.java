package revi1337.onsquad.member.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.image.error.exception.AttachmentValidationException;
import revi1337.onsquad.inrastructure.s3.application.FileUploadManager;
import revi1337.onsquad.member.application.event.MemberUpdateEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberUpdateEventListener {

    private final FileUploadManager memberS3FileManager;
    private final MemberRepository memberRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleMemberUpdateEvent(MemberUpdateEvent event) {
        MultipartFile file = event.file();
        Member member = event.member();
        try {
            String uploadUrl;
            if (member.getProfileImage().isEmpty()) {
                uploadUrl = memberS3FileManager.uploadFile(file.getBytes(), file.getOriginalFilename());
            } else {
                uploadUrl = memberS3FileManager.updateFile(member.getProfileImage(), file.getBytes(), file.getOriginalFilename());
            }
            member.updateProfileImage(uploadUrl);
            memberRepository.saveAndFlush(member);
            log.info("[사용자 프로필 이미지 업데이트 성공] : member_id = {}", member.getId());
        } catch (AttachmentValidationException.UnsupportedAttachmentType e) {
            log.error("[사용자 프로필 이미지 업데이트 실패] : member_id = {}, message = {}", member.getId(), e.getErrorMessage());
        } catch (Exception ignored) {
            log.error("[사용자 프로필 이미지 업데이트 실패] : member_id = {}", member.getId());
        }
    }
}
