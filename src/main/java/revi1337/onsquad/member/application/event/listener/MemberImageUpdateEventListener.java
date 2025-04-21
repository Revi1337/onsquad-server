package revi1337.onsquad.member.application.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.inrastructure.file.application.FileStorageManager;
import revi1337.onsquad.inrastructure.file.support.RecycleBin;
import revi1337.onsquad.member.application.event.MemberImageUpdateEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberImageUpdateEventListener {

    private final MemberRepository memberRepository;
    private final FileStorageManager memberS3StorageManager;

    @EventListener(MemberImageUpdateEvent.class)
    public void handleMemberImageUpdateEvent(MemberImageUpdateEvent event) {
        Member member = event.member();
        MultipartFile file = event.file();

        if (member.hasDefaultProfileImage()) {
            String uploadUrl = memberS3StorageManager.upload(file);
            member.updateProfileImage(uploadUrl);
            memberRepository.saveAndFlush(member);
            return;
        }

        memberS3StorageManager.upload(file, member.getProfileImage());
        RecycleBin.append(member.getProfileImage());
    }
}
