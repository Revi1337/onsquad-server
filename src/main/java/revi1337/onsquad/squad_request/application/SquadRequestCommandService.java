package revi1337.onsquad.squad_request.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_member.application.SquadMemberAccessPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.entity.SquadMemberFactory;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadRequestCommandService {

    private final MemberRepository memberRepository;
    private final SquadRepository squadRepository;
    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadRequestAccessPolicy squadRequestAccessPolicy;
    private final SquadRequestRepository squadRequestRepository;

    public void request(Long memberId, Long squadId) {
        squadMemberAccessPolicy.ensureMemberNotInSquad(memberId, squadId);
        if (squadRequestAccessPolicy.isRequestAbsent(memberId, squadId)) {
            Squad squadRef = squadRepository.getReferenceById(squadId);
            Member memberRef = memberRepository.getReferenceById(memberId);
            squadRequestRepository.save(SquadRequest.of(squadRef, memberRef, LocalDateTime.now()));
        }
    }

    public void acceptRequest(Long memberId, Long squadId, Long requestId) {
        SquadRequest request = squadRequestAccessPolicy.ensureRequestExistsAndGet(requestId);
        squadRequestAccessPolicy.ensureMatchSquad(request, squadId);
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadRequestAccessPolicy.ensureAcceptable(squadMember);
        Squad squad = request.getSquad();
        squad.addMembers(SquadMemberFactory.general(request.getMember(), LocalDateTime.now()));
        squadRequestRepository.deleteById(requestId);
    }

    public void rejectRequest(Long memberId, Long squadId, Long requestId) {
        SquadRequest request = squadRequestAccessPolicy.ensureRequestExistsAndGet(requestId);
        squadRequestAccessPolicy.ensureMatchSquad(request, squadId);
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadRequestAccessPolicy.ensureRejectable(squadMember);
        squadRequestRepository.deleteById(requestId);
    }

    public void cancelMyRequest(Long memberId, Long squadId) {
        squadRequestRepository.deleteBySquadIdMemberId(squadId, memberId);
    }
}
