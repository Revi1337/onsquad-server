package revi1337.onsquad.crew_member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

@Import({CrewMemberQueryDslRepository.class, CrewMemberRepositoryImpl.class})
class CrewMemberRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Nested
    @DisplayName("CrewMember 저장을 테스트한다.")
    class Save {

        @Test
        @DisplayName("CrewMember 저장에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());

            crewMemberRepository.save(CrewMember.forGeneral(CREW, ANDONG, LocalDateTime.now()));

            assertThat(crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), ANDONG.getId())).isPresent();
        }
    }

    @Nested
    @DisplayName("CrewMember 단일 조회를 테스트한다.")
    class Find {

        @Test
        @DisplayName("CrewMember 단일 조회에 성공한다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberRepository.save(CrewMember.forGeneral(CREW, ANDONG, LocalDateTime.now()));

            Optional<CrewMember> CREW_MEMBER = crewMemberRepository
                    .findByCrewIdAndMemberId(CREW.getId(), ANDONG.getId());

            assertThat(CREW_MEMBER).isPresent();
        }

        @Test
        @DisplayName("CrewMember 를 찾지 못하면 예외가 발생한다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());

            assertThatThrownBy(() -> crewMemberRepository
                    .getByCrewIdAndMemberId(CREW.getId(), ANDONG.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
        }
    }

    @Nested
    @DisplayName("CrewMember 존재여부를 테스트한다.")
    class Exists {

        @Test
        @DisplayName("CrewMember 가 존재하면 true 를 반환한다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberRepository.save(CrewMember.forGeneral(CREW, ANDONG, LocalDateTime.now()));

            Boolean exists = crewMemberRepository.existsByMemberIdAndCrewId(ANDONG.getId(), CREW.getId());

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("CrewMember 가 존재하지 않으면 false 를 반환한다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());

            Boolean exists = crewMemberRepository.existsByMemberIdAndCrewId(ANDONG.getId(), CREW.getId());

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("CrewMember 삭제를 테스트한다.")
    class Delete {

        @Test
        @DisplayName("특정 Crew 에 속한 모든 CrewMember 를 삭제한다. (Crew 삭제시 호출됨.)")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberRepository.save(CrewMember.forGeneral(CREW, ANDONG, LocalDateTime.now()));

            crewMemberRepository.deleteAllByCrewId(CREW.getId());

            assertThat(crewMemberJpaRepository.findAllByCrewId(CREW.getId())).hasSize(0);
        }
    }

    @Nested
    @DisplayName("Projection 쿼리를 테스트한다.")
    class FetchDto {

        @Test
        @DisplayName("Crew 에 속한 CrewMember 들 조회에 성공한다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            LocalDateTime NOW = LocalDateTime.now();
            crewMemberRepository.save(CrewMember.forGeneral(CREW, ANDONG, NOW));
            crewMemberRepository.save(CrewMember.forGeneral(CREW, KWANGWON, NOW.plusMinutes(1)));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 5);

            Page<CrewMemberDomainDto> DTOS = crewMemberRepository
                    .findManagedCrewMembersByCrewId(CREW.getId(), PAGE_REQUEST);

            assertAll(() -> {
                assertThat(DTOS).hasSize(3);

                assertThat(DTOS.getContent().get(0).memberInfo().id()).isEqualTo(KWANGWON.getId());
                assertThat(DTOS.getContent().get(0).memberInfo().nickname()).isEqualTo(KWANGWON_NICKNAME);
                assertThat(DTOS.getContent().get(0).memberInfo().mbti()).isSameAs(KWANGWON_MBTI);
                assertThat(DTOS.getContent().get(0).participantAt()).isEqualTo(NOW.plusMinutes(1));

                assertThat(DTOS.getContent().get(1).memberInfo().id()).isEqualTo(ANDONG.getId());
                assertThat(DTOS.getContent().get(1).memberInfo().nickname()).isEqualTo(ANDONG_NICKNAME);
                assertThat(DTOS.getContent().get(1).memberInfo().mbti()).isSameAs(ANDONG_MBTI);
                assertThat(DTOS.getContent().get(1).participantAt()).isEqualTo(NOW);

                assertThat(DTOS.getContent().get(2).memberInfo().id()).isEqualTo(REVI.getId());
                assertThat(DTOS.getContent().get(2).memberInfo().nickname()).isEqualTo(REVI_NICKNAME);
                assertThat(DTOS.getContent().get(2).memberInfo().mbti()).isSameAs(REVI_MBTI);
                assertThat(DTOS.getContent().get(2).participantAt()).isNotNull();
            });
        }
    }
}
