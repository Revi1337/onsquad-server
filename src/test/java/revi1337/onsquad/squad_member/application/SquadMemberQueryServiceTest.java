package revi1337.onsquad.squad_member.application;

import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;

class SquadMemberQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Autowired
    private SquadMemberQueryService squadMemberQueryService;

}
