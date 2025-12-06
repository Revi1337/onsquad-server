package revi1337.onsquad.member.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.application.file.FileStorageManager;
import revi1337.onsquad.infrastructure.aws.s3.support.RecycleBin;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.event.MemberImageDeleteEvent;
import revi1337.onsquad.member.domain.event.MemberImageUpdateEvent;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberImageListener {

    private final MemberRepository memberRepository;
    private final FileStorageManager memberS3StorageManager;

    @EventListener(MemberImageUpdateEvent.class)
    public void handleMemberImageUpdateEvent(MemberImageUpdateEvent event) {
        Member member = event.member();
        MultipartFile file = event.file();
        if (member.hasDefaultImage()) {
            String uploadUrl = memberS3StorageManager.upload(file);
            member.updateImage(uploadUrl);
            memberRepository.saveAndFlush(member);
            return;
        }
        memberS3StorageManager.upload(file, member.getProfileImage());
    }

    @EventListener(MemberImageDeleteEvent.class)
    public void handleMemberImageDeleteEvent(MemberImageDeleteEvent event) {
        Member member = event.member();
        RecycleBin.append(member.getProfileImage());
        member.changeDefaultImage();
        memberRepository.saveAndFlush(member);
    }
}
