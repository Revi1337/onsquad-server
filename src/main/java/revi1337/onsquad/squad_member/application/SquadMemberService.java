//package revi1337.onsquad.squad_member.application;
//
//import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.CANNOT_LEAVE_LEADER;
//
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import revi1337.onsquad.crew_member.domain.entity.CrewMember;
//import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
//import revi1337.onsquad.squad_member.application.dto.EnrolledSquadDto;
//import revi1337.onsquad.squad_member.application.dto.SquadMemberDto;
//import revi1337.onsquad.squad_member.domain.entity.SquadMember;
//import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
//import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
//
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//@Service
//public class SquadMemberService {
//
//    private static final int LEADER_LIMIT_THRESHOLD = 2;
//
//    private final SquadMemberRepository squadMemberRepository;
//    private final CrewMemberRepository crewMemberRepository;
//
//    public List<EnrolledSquadDto> fetchAllJoinedSquads(Long memberId) {
//        return squadMemberRepository.fetchAllJoinedSquadsByMemberId(memberId).stream()
//                .map(EnrolledSquadDto::from)
//                .toList();
//    }
//
//    public List<SquadMemberDto> fetchAllBySquadId(Long memberId, Long crewId, Long squadId) {
//        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
//        SquadMember ignored = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
//
//        return squadMemberRepository.fetchAllBySquadId(squadId).stream()
//                .map(SquadMemberDto::from)
//                .toList();
//    }
//
//    @Transactional
//    public void leaveSquad(Long memberId, Long crewId, Long squadId) {
//        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
//        SquadMember squadMember = squadMemberRepository
//                .getWithSquadBySquadIdAndCrewMemberId(squadId, crewMember.getId());
//        if (squadMember.isLeader() && squadMemberRepository.countBySquadId(squadId) >= LEADER_LIMIT_THRESHOLD) {
//            throw new SquadMemberBusinessException.CannotLeaveLeader(CANNOT_LEAVE_LEADER);
//        }
//
//        squadMemberRepository.delete(squadMember);
//        squadMemberRepository.flush();
//        squadMember.leaveSquad();
//    }
//}
