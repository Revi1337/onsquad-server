package revi1337.onsquad.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

class CrewJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Nested
    @DisplayName("findById 를 테스트한다.")
    class FindById {

        @Test
        @DisplayName("Crew 조회에 성공한다.")
        void findById() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            clearPersistenceContext();

            Optional<Crew> optionalCrew = crewJpaRepository.findById(CREW.getId());

            assertThat(optionalCrew).isPresent();
        }
    }

    @Nested
    @DisplayName("existsByName 를 테스트한다.")
    class ExistsByName {

        @Test
        @DisplayName("Crew Name 으로 조회했을 때, Crew 가 존재하면 true 를 반환한다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            crewJpaRepository.save(CREW(REVI));

            boolean exists = crewJpaRepository.existsByName(CREW_NAME);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Crew Name 으로 조회했을 때, Crew 가 존재하지 않으면 false 를 반환한다.")
        void success2() {
            boolean exists = crewJpaRepository.existsByName(CREW_NAME);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("deleteById 를 테스트한다.")
    class DeleteById {

        @Test
        @DisplayName("Crew 삭제에 성공한다.")
        void existsByName1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));

            crewJpaRepository.deleteById(CREW.getId());

            assertThat(crewJpaRepository.findById(CREW.getId())).isEmpty();
        }
    }
}