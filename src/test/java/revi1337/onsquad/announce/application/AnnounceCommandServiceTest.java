package revi1337.onsquad.announce.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceUpdateDto;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.error.AnnounceErrorCode;
import revi1337.onsquad.announce.domain.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.event.AnnounceDeleteEvent;
import revi1337.onsquad.announce.domain.event.AnnouncePinnedEvent;
import revi1337.onsquad.announce.domain.event.AnnounceUpdateEvent;
import revi1337.onsquad.announce.domain.repository.AnnounceJpaRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

class AnnounceCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private AnnounceJpaRepository announceRepository;

    @Autowired
    private AnnounceCommandService announceCommandService;

    @Autowired
    private ApplicationEvents events;

    @Nested
    class newAnnounce {

        @Test
        @DisplayName("manager 나 owner 는 공지사항을 작성할 수 있다.")
        void success() {
            Member revi = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(revi));
            clearPersistenceContext();
            AnnounceCreateDto dto = new AnnounceCreateDto("title", "content");

            announceCommandService.newAnnounce(revi.getId(), crew.getId(), dto);

            assertSoftly(softly -> {
                clearPersistenceContext();
                softly.assertThat(announceRepository.findAll()).hasSize(1);
                softly.assertThat(events.stream(AnnounceCreateEvent.class).count()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("general 은 공지사항을 작성할 수 없다.")
        void fail() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            clearPersistenceContext();
            AnnounceCreateDto dto = new AnnounceCreateDto("title", "content");

            assertThatThrownBy(() -> announceCommandService.newAnnounce(andong.getId(), savedCrew.getId(), dto))
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_CREATE_AUTHORITY.getDescription());
        }
    }

    @Nested
    class updateAnnounce {

        @Test
        @DisplayName("owner 는 공지사항을 수정할 수 있다.")
        void success() {
            Member revi = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(revi));
            Announce announce = announceRepository.save(createCrewAnnounce(crew, revi));
            clearPersistenceContext();
            AnnounceUpdateDto dto = new AnnounceUpdateDto("update-title", "update-content");

            announceCommandService.updateAnnounce(revi.getId(), crew.getId(), announce.getId(), dto);

            assertSoftly(softly -> {
                clearPersistenceContext();
                assertThat(announceRepository.findAll().get(0).getTitle().getValue()).isEqualTo(dto.title());
                assertThat(announceRepository.findAll().get(0).getContent()).isEqualTo(dto.content());
                assertThat(events.stream(AnnounceUpdateEvent.class).count()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("공지사항이 속한 크루가 다르면 수정할 수 없다.")
        void fail1() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew1 = crewRepository.save(createCrew(revi));
            Announce announce = announceRepository.save(createCrewAnnounce(crew1, revi));
            Crew crew2 = crewRepository.save(createCrew(andong));
            clearPersistenceContext();
            AnnounceUpdateDto dto = new AnnounceUpdateDto("update-title", "update-content");

            assertThatThrownBy(() -> announceCommandService
                    .updateAnnounce(revi.getId(), crew2.getId(), announce.getId(), dto))
                    .hasMessage(AnnounceErrorCode.MISMATCH_CREW_REFERENCE.getDescription());
        }

        @Test
        @DisplayName("general 는 공지사항을 수정할 수 없다.")
        void fail2() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, revi));
            clearPersistenceContext();
            AnnounceUpdateDto dto = new AnnounceUpdateDto("update-title", "update-content");

            assertThatThrownBy(() -> announceCommandService
                    .updateAnnounce(andong.getId(), savedCrew.getId(), announce.getId(), dto))
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY.getDescription());
        }

        @Test
        @DisplayName("manager 는 owner 의 공지사항을 수정할 수 없다.")
        void fail3() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, revi));
            clearPersistenceContext();
            AnnounceUpdateDto dto = new AnnounceUpdateDto("update-title", "update-content");

            assertThatThrownBy(() -> announceCommandService
                    .updateAnnounce(andong.getId(), savedCrew.getId(), announce.getId(), dto))
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_UPDATE_AUTHORITY.getDescription());
        }
    }

    @Nested
    class deleteAnnounce {

        @Test
        @DisplayName("owner 는 공지사항을 삭제할 수 있다.")
        void success() {
            Member revi = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(revi));
            Announce announce = announceRepository.save(createCrewAnnounce(crew, revi));
            clearPersistenceContext();

            announceCommandService.deleteAnnounce(revi.getId(), crew.getId(), announce.getId());

            assertSoftly(softly -> {
                clearPersistenceContext();
                assertThat(announceRepository.findAll().size()).isZero();
                assertThat(events.stream(AnnounceDeleteEvent.class).count()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("공지사항이 속한 크루가 다르면 삭제할 수 없다.")
        void fail1() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew1 = crewRepository.save(createCrew(revi));
            Announce announce = announceRepository.save(createCrewAnnounce(crew1, revi));
            Crew crew2 = crewRepository.save(createCrew(andong));
            clearPersistenceContext();

            assertThatThrownBy(() -> announceCommandService
                    .deleteAnnounce(revi.getId(), crew2.getId(), announce.getId()))
                    .hasMessage(AnnounceErrorCode.MISMATCH_CREW_REFERENCE.getDescription());
        }

        @Test
        @DisplayName("general 는 공지사항을 삭제할 수 없다.")
        void fail2() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, revi));
            clearPersistenceContext();

            assertThatThrownBy(() -> announceCommandService
                    .deleteAnnounce(andong.getId(), savedCrew.getId(), announce.getId()))
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY.getDescription());
        }

        @Test
        @DisplayName("manager 는 owner 의 공지사항을 삭제할 수 없다.")
        void fail3() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, revi));
            clearPersistenceContext();

            assertThatThrownBy(() -> announceCommandService
                    .deleteAnnounce(andong.getId(), savedCrew.getId(), announce.getId()))
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_DELETE_AUTHORITY.getDescription());
        }
    }

    @Nested
    class changePinState {

        @Test
        @DisplayName("owner 는 공지사항을 상단고정할 수 있다.")
        void success() {
            Member revi = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(revi));
            Announce announce = announceRepository.save(createCrewAnnounce(crew, revi));
            clearPersistenceContext();

            announceCommandService.changePinState(revi.getId(), crew.getId(), announce.getId(), true);

            assertSoftly(softly -> {
                clearPersistenceContext();
                assertThat(announceRepository.findAll().get(0).isPinned()).isTrue();
                assertThat(events.stream(AnnouncePinnedEvent.class).count()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("공지사항이 속한 크루가 다르면 상단고정할 수 없다.")
        void fail1() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew1 = crewRepository.save(createCrew(revi));
            Announce announce = announceRepository.save(createCrewAnnounce(crew1, revi));
            Crew crew2 = crewRepository.save(createCrew(andong));
            clearPersistenceContext();

            assertThatThrownBy(() -> announceCommandService
                    .changePinState(revi.getId(), crew2.getId(), announce.getId(), true))
                    .hasMessage(AnnounceErrorCode.MISMATCH_CREW_REFERENCE.getDescription());
        }

        @Test
        @DisplayName("owner 가 아닌 사용자는 공지사항 상단고정을 할 수 없다.")
        void fail2() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, revi));
            clearPersistenceContext();

            assertThatThrownBy(() -> announceCommandService
                    .changePinState(andong.getId(), savedCrew.getId(), announce.getId(), true))
                    .hasMessage(AnnounceErrorCode.INSUFFICIENT_PIN_AUTHORITY.getDescription());
        }
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    private Announce createCrewAnnounce(Crew crew, Member revi) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, revi);
    }
}
