package revi1337.onsquad.announce.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

class AnnounceJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private AnnounceJpaRepository announceJpaRepository;

    @Test
    @DisplayName("Announce Id 와 Crew Id 로 Announce 조회에 성공한다.")
    void success() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MEMBER));

        Optional<Announce> OPTIONAL_ANNOUNCE = announceJpaRepository.findByIdAndCrewId(ANNOUNCE.getId(), CREW.getId());

        assertThat(OPTIONAL_ANNOUNCE).isPresent();
    }

    @Test
    @DisplayName("Announce Id 와 Crew Id 로 Announce 조회에 실패한다.")
    void fail() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MEMBER));
        Long DUMMY_CREW_ID = 100L;

        Optional<Announce> OPTIONAL_ANNOUNCE = announceJpaRepository.findByIdAndCrewId(ANNOUNCE.getId(), DUMMY_CREW_ID);

        assertThat(OPTIONAL_ANNOUNCE).isEmpty();
    }
}
