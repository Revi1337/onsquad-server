package revi1337.onsquad.crew_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew_member.application.response.CrewMemberResponse;
import revi1337.onsquad.crew_member.application.response.MyParticipantResponse;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrewMemberService {

    private final CrewMemberAccessor crewMemberAccessor;

    public PageResponse<CrewMemberResponse> fetchParticipants(Long memberId, Long crewId, Pageable pageable) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewMemberPolicy.ensureReadParticipantsAccessible(me);

        Page<CrewMemberResponse> participants = crewMemberAccessor.fetchParticipantsByCrewId(crewId, pageable)
                .map(participant -> {
                    boolean isMe = CrewMemberPolicy.isMe(me, participant); // TODO canKick 과 canOwnerDelegate 는 아직 정의되지 않음. 일단 임의로 지정
                    boolean canKick = CrewMemberPolicy.canKick(me, participant);
                    boolean canOwnerDelegate = CrewMemberPolicy.canOwnerDelegate(me, participant);

                    return CrewMemberResponse.from(isMe, canKick, canOwnerDelegate, participant);
                });

        return PageResponse.from(participants);
    }

    public List<MyParticipantResponse> fetchMyParticipatingCrews(Long memberId) {
        return crewMemberAccessor.fetchParticipantCrews(memberId).stream()
                .map(MyParticipantResponse::from)
                .toList();
    }
}
