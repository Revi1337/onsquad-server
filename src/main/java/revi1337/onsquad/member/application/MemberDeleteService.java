package revi1337.onsquad.member.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.event.MemberDeleteEvent;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberDeleteService {

    private final MemberAccessor memberAccessor;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void deleteMember(Long memberId) {
        Member member = memberAccessor.getById(memberId);
        memberRepository.deleteById(memberId);
        eventPublisher.publishEvent(new MemberDeleteEvent(memberId, collectImagesToDelete(memberId, member)));
    }

    private List<String> collectImagesToDelete(Long memberId, Member member) {
        List<String> imageUrls = new ArrayList<>();
        if (member.hasImage()) {
            imageUrls.add(member.getProfileImage());
        }

        crewRepository.findAllByMemberId(memberId).stream()
                .filter(Crew::hasImage)
                .forEach(crew -> imageUrls.add(crew.getImageUrl()));

        return imageUrls;
    }
}
