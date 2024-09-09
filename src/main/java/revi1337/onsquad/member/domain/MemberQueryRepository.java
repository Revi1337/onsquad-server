package revi1337.onsquad.member.domain;

import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findMemberWithRefCrewAndRefCrewMembersById(Long memberId);

}
