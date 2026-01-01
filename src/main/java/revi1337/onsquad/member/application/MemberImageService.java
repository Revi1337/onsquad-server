package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.domain.entity.Member;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberImageService {

    private final MemberAccessor memberAccessor;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void updateImage(Long memberId, String newImageUrl) {
        Member member = memberAccessor.getById(memberId);
        if (member.hasImage()) {
            applicationEventPublisher.publishEvent(new FileDeleteEvent(member.getProfileImage()));
        }
        member.updateImage(newImageUrl);
    }

    public void deleteImage(Long memberId) {
        Member member = memberAccessor.getById(memberId);
        if (member.hasImage()) {
            applicationEventPublisher.publishEvent(new FileDeleteEvent(member.getProfileImage()));
            member.deleteImage();
        }
    }
}
