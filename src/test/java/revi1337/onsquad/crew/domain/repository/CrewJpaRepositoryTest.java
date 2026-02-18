package revi1337.onsquad.crew.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Test
    void existsByName() {
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));

        assertThat(crewJpaRepository.existsByName(crew.getName())).isTrue();
    }

    @Test
    void findAllByMemberId() {
        Member member = memberJpaRepository.save(createRevi());
        crewJpaRepository.saveAll(List.of(
                createCrew(member),
                createCrew(member),
                createCrew(member),
                createCrew(member)
        ));

        assertThat(crewJpaRepository.findAllByMemberId(member.getId())).hasSize(4);
    }

    @Test
    void deleteByIdIn() {
        Member member = memberJpaRepository.save(createRevi());
        Crew crew1 = crewJpaRepository.save(createCrew(member));
        Crew crew2 = crewJpaRepository.save(createCrew(member));
        Crew crew3 = crewJpaRepository.save(createCrew(member));

        crewMemberJpaRepository.deleteByMemberId(member.getId());
        assertThat(crewJpaRepository.deleteByIdIn(List.of(crew1.getId(), crew2.getId(), crew3.getId())))
                .isEqualTo(3);
    }

    @Test
    void decrementCountByMemberId() {
        Member revi = memberJpaRepository.save(createRevi());
        Member andong = memberJpaRepository.save(createAndong());
        Crew crew1 = createCrew(revi);
        crew1.addCrewMember(createGeneralCrewMember(crew1, andong));
        crewJpaRepository.save(crew1);
        assertThat(crew1.getCurrentSize()).isEqualTo(2);

        crewJpaRepository.decrementCountByMemberId(andong.getId());
        clearPersistenceContext();

        assertThat(crewJpaRepository.findById(crew1.getId()).get().getCurrentSize()).isEqualTo(1);
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
