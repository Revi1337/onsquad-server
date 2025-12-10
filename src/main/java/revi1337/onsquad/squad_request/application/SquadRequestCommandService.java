package revi1337.onsquad.squad_request.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.entity.SquadMemberFactory;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;
import revi1337.onsquad.squad_request.error.SquadRequestErrorCode;
import revi1337.onsquad.squad_request.error.exception.SquadRequestBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadRequestCommandService { // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에

    private final MemberRepository memberRepository;
    private final SquadRepository squadRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final SquadRequestRepository squadRequestRepository;

    public void request(Long memberId, Long squadId) {
        validateMemberNotInSquad(memberId, squadId);
        if (squadRequestRepository.findBySquadIdAndMemberId(squadId, memberId).isEmpty()) {
            Squad squadRef = squadRepository.getReferenceById(squadId);
            Member memberRef = memberRepository.getReferenceById(memberId);
            squadRequestRepository.save(SquadRequest.of(squadRef, memberRef, LocalDateTime.now()));
        }
    }

    public void acceptRequest(Long memberId, Long squadId, Long requestId) {
        SquadMember squadMember = validateMemberInSquadAndGet(memberId, squadId);
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(SquadMemberErrorCode.NOT_LEADER);
        }
        SquadRequest request = squadRequestRepository.findByIdWithSquad(requestId)
                .orElseThrow(() -> new SquadRequestBusinessException.NotFound(SquadRequestErrorCode.NOT_FOUND));
        if (request.mismatchSquadId(squadId)) {
            throw new SquadRequestBusinessException.MismatchReference(SquadRequestErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
        Squad squad = request.getSquad();
        squad.addMembers(SquadMemberFactory.general(request.getMember(), LocalDateTime.now()));
        squadRequestRepository.deleteById(requestId);
    }

    public void rejectRequest(Long memberId, Long squadId, Long requestId) {
        SquadMember squadMember = validateMemberInSquadAndGet(memberId, squadId);
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(SquadMemberErrorCode.NOT_LEADER);
        }
        SquadRequest request = validateSquadRequestExistsAndGet(requestId);
        if (request.mismatchSquadId(squadId)) {
            throw new SquadRequestBusinessException.MismatchReference(SquadRequestErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
        squadRequestRepository.deleteById(requestId);
    }

    public void cancelMyRequest(Long memberId, Long squadId) {
        squadRequestRepository.deleteBySquadIdMemberId(squadId, memberId);
    }

    private void validateMemberNotInSquad(Long memberId, Long squadId) {  // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        if (squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId).isPresent()) {
            throw new SquadMemberBusinessException.AlreadyParticipant(SquadMemberErrorCode.ALREADY_JOIN);
        }
    }

    private SquadMember validateMemberInSquadAndGet(Long memberId, Long squadId) {
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }

    private SquadRequest validateSquadRequestExistsAndGet(Long requestId) {
        return squadRequestRepository.findById(requestId)
                .orElseThrow(() -> new SquadRequestBusinessException.NotFound(SquadRequestErrorCode.NOT_FOUND));
    }
}
