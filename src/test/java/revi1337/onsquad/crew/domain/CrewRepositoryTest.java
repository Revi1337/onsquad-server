package revi1337.onsquad.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.MemberRepositoryImpl;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;

@Import({CrewRepositoryImpl.class, MemberRepositoryImpl.class, CrewQueryDslRepository.class})
class CrewRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("Crew 조회를 테스트한다.")
    class Find {

        @Test
        @DisplayName("Crew id 로 조회했을 때, Crew 가 존재하는지 확인한다.")
        void findById() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));

            Optional<Crew> optionalCrew = crewRepository.findById(CREW.getId());

            assertThat(optionalCrew).isPresent();
        }

        @Test
        @DisplayName("Crew Id 로 조회하면, Crew 정보를 담은 DTO 를 반환한다.")
        void findCrewById() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));

            Optional<CrewInfoDomainDto> OPTIONAL_CREW = crewRepository.findCrewById(CREW.getId());

            assertAll(() -> {
                assertThat(OPTIONAL_CREW).isPresent();
                assertThat(OPTIONAL_CREW.get().getId()).isEqualTo(CREW.getId());
                assertThat(OPTIONAL_CREW.get().getName()).isEqualTo(CREW_NAME);
                assertThat(OPTIONAL_CREW.get().getIntroduce()).isEqualTo(CREW_INTRODUCE);
                assertThat(OPTIONAL_CREW.get().getDetail()).isEqualTo(CREW_DETAIL);
                assertThat(OPTIONAL_CREW.get().getImageUrl()).isNull();
                assertThat(OPTIONAL_CREW.get().getKakaoLink()).isNull();
                assertThat(OPTIONAL_CREW.get().getMemberCnt()).isEqualTo(1);
                assertThat(OPTIONAL_CREW.get().getCrewOwner()).isEqualTo(new SimpleMemberInfoDomainDto(
                        REVI.getId(),
                        new Nickname(REVI_NICKNAME_VALUE),
                        Mbti.ISTP
                ));
            });
        }

        @Test
        @DisplayName("Crew Id 로 조회했을 때, 없으면 오류를 반환한다.")
        void findCrewById2() {
            Long DUMMY_CREW_ID = 1L;

            assertThatThrownBy(() -> crewRepository.getCrewById(DUMMY_CREW_ID))
                    .isExactlyInstanceOf(CrewBusinessException.NotFoundById.class);
        }
    }

    @Nested
    @DisplayName("Exists 를 테스트한다.")
    class Exists {

        @Test
        @DisplayName("Crew Name 으로 조회했을 때, Crew 가 존재하면 true 를 반환한다.")
        void existsByName1() {
            Member REVI = memberRepository.save(REVI());
            crewRepository.save(CREW(REVI));

            boolean exists = crewRepository.existsByName(CREW_NAME);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Crew Name 으로 조회했을 때, Crew 가 존재하지 않으면 false 를 반환한다.")
        void existsByName2() {
            boolean exists = crewRepository.existsByName(CREW_NAME);

            assertThat(exists).isFalse();
        }
    }
}
