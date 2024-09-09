package revi1337.onsquad.crew_member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

import java.util.Optional;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_PARTICIPANT;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long>, CrewMemberQueryRepository {

    @Query("select cm from CrewMember as cm where cm.crew.id = :crewId and cm.member.id = :memberId")
    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    default CrewMember getByCrewIdAndMemberId(Long crewId, Long memberId) {
        return findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT));
    }
}


//package revi1337.onsquad.crew_member.domain;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
//
//import java.util.Optional;
//
//import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_PARTICIPANT;
//
//public interface CrewMemberRepository extends JpaRepository<CrewMember, Long>, CrewMemberQueryRepository {
//
//    @Query("select cm from CrewMember as cm where cm.crew.id = :crewId and cm.member.id = :memberId")
//    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);
//
//    default CrewMember getByCrewIdAndMemberId(Long crewId, Long memberId) {
//        return findByCrewIdAndMemberId(crewId, memberId)
//                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT));
//    }
//}
