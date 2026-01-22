package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberContextDisposer {

    private final SquadRepository squadRepository;
    private final SquadRequestRepository squadRequestRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final SquadCommentRepository squadCommentRepository;

    private final CrewRepository crewRepository;
    private final CrewRequestRepository crewRequestRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;

    public void disposeMemberActivityFromSquads(Long memberId) {
        squadRepository.decrementCountByMemberId(memberId);
        squadRequestRepository.deleteByMemberId(memberId);
        squadMemberRepository.deleteByMemberId(memberId);
        squadCommentRepository.deleteByMemberId(memberId);
    }

    public void disposeMemberActivityFromCrews(Long memberId) {
        crewRepository.decrementCountByMemberId(memberId);
        crewRequestRepository.deleteByMemberId(memberId);
        crewMemberRepository.deleteByMemberId(memberId);
        announceRepository.markMemberAsNull(memberId);
    }
}
