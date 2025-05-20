package revi1337.onsquad.squad.application;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.category.domain.vo.CategoryType.ALL;
import static revi1337.onsquad.category.domain.vo.CategoryType.GAME;
import static revi1337.onsquad.category.domain.vo.CategoryType.MANGACAFE;
import static revi1337.onsquad.category.domain.vo.CategoryType.MOVIE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CAPACITY_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_DISCORD_LINK_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_TITLE_VALUE;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad_category.domain.SquadCategoryJpaRepository;
import revi1337.onsquad.squad_member.domain.SquadMemberJpaRepository;

class SquadCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Autowired
    private SquadRepository squadRepository;

    @Autowired
    private SquadCommandService squadCommandService;

    @Autowired
    private SquadCategoryJpaRepository squadCategoryJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Nested
    @DisplayName("스쿼드 생성을 테스트한다.")
    class Create {

        @Test
        @DisplayName("스쿼드 생성에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadCreateDto CREATE_DTO = new SquadCreateDto(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    SQUAD_CAPACITY_VALUE,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(GAME, MOVIE),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            clearPersistenceContext();

            Long SQUAD_ID = squadCommandService.newSquad(ANDONG.getId(), CREW.getId(), CREATE_DTO);

            entityManager.flush();
            entityManager.clear();
            assertThat(squadRepository.findById(SQUAD_ID)).isPresent();
            assertThat(squadCategoryJpaRepository.findAllBySquadId(SQUAD_ID)).hasSize(2);
            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndCrewMemberId(SQUAD_ID, GENERAL_CREW_MEMBER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("ALL 카테고리를 제외하고 스쿼드 생성에 성공한다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadCreateDto CREATE_DTO = new SquadCreateDto(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    SQUAD_CAPACITY_VALUE,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(GAME, MOVIE, MANGACAFE, ALL),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            clearPersistenceContext();

            Long SQUAD_ID = squadCommandService.newSquad(ANDONG.getId(), CREW.getId(), CREATE_DTO);

            entityManager.flush();
            entityManager.clear();
            assertThat(squadRepository.findById(SQUAD_ID)).isPresent();
            assertThat(squadCategoryJpaRepository.findAllBySquadId(SQUAD_ID)).hasSize(3);
            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndCrewMemberId(SQUAD_ID, GENERAL_CREW_MEMBER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("ALL 카테고리 하나면, 카테고리 없이 스쿼드가 생성된다.")
        void success3() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadCreateDto CREATE_DTO = new SquadCreateDto(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    SQUAD_CAPACITY_VALUE,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(ALL),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            clearPersistenceContext();

            Long SQUAD_ID = squadCommandService.newSquad(ANDONG.getId(), CREW.getId(), CREATE_DTO);

            entityManager.flush();
            entityManager.clear();
            assertThat(squadRepository.findById(SQUAD_ID)).isPresent();
            assertThat(squadCategoryJpaRepository.findAllBySquadId(SQUAD_ID)).hasSize(0);
            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndCrewMemberId(SQUAD_ID, GENERAL_CREW_MEMBER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("카테고리가 없으면, 카테고리 없이 스쿼드가 생성된다.")
        void success4() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadCreateDto CREATE_DTO = new SquadCreateDto(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    SQUAD_CAPACITY_VALUE,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            clearPersistenceContext();

            Long SQUAD_ID = squadCommandService.newSquad(ANDONG.getId(), CREW.getId(), CREATE_DTO);

            entityManager.flush();
            entityManager.clear();
            assertThat(squadRepository.findById(SQUAD_ID)).isPresent();
            assertThat(squadCategoryJpaRepository.findAllBySquadId(SQUAD_ID)).hasSize(0);
            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndCrewMemberId(SQUAD_ID, GENERAL_CREW_MEMBER.getId()))
                    .isPresent();
        }
    }
}