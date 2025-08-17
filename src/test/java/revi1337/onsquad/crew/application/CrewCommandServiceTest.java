package revi1337.onsquad.crew.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_WITH_IMAGE;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_DETAIL;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_INTRODUCE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_NAME;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.DEFAULT_ORIGINAL_FILENAME;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.RequestFixture.DEFAULT_MULTIPART_NAME;
import static revi1337.onsquad.common.fixture.RequestFixture.PNG_MULTIPART;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.application.event.CrewImageDeleteEvent;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.inrastructure.file.application.FileStorageManager;
import revi1337.onsquad.inrastructure.file.application.event.FileDeleteEvent;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

@Sql({"/h2-hashtag.sql"})
class CrewCommandServiceTest extends ApplicationLayerTestSupport {

    @MockBean(name = "crewS3StorageManager")
    private FileStorageManager crewS3StorageManager;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private CrewCommandService crewCommandService;

    @Nested
    @DisplayName("Crew 생성을 테스트한다.")
    class NewCrew {

        @Test
        @DisplayName("Crew 생성에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            CrewCreateDto CREATE_DTO = new CrewCreateDto(
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    List.of(HashtagType.ACTIVE),
                    CREW_KAKAO_LINK_VALUE
            );

            Long CREW_ID = crewCommandService.newCrew(REVI.getId(), CREATE_DTO, CREW_IMAGE_LINK_VALUE);
            Optional<Crew> OPTIONAL_CREW = crewJpaRepository.findById(CREW_ID);
            assertThat(OPTIONAL_CREW).isPresent();
        }

        @Test
        @DisplayName("Crew 가 이미 존재하면 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            crewJpaRepository.save(CREW(REVI));
            CrewCreateDto CREATE_DTO = new CrewCreateDto(
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    List.of(HashtagType.ACTIVE),
                    CREW_KAKAO_LINK_VALUE
            );

            assertThatThrownBy(() -> crewCommandService.newCrew(REVI.getId(), CREATE_DTO, CREW_IMAGE_LINK_VALUE))
                    .isExactlyInstanceOf(CrewBusinessException.AlreadyExists.class);
        }
    }

    @Nested
    @DisplayName("Crew 업데이트를 테스트한다.")
    class UpdateCrew {

        @Test
        @DisplayName("Crew 업데이트에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewUpdateDto UPDATE_DTO = new CrewUpdateDto(
                    CHANGED_CREW_NAME_VALUE,
                    CHANGED_CREW_INTRODUCE_VALUE,
                    CHANGED_CREW_DETAIL_VALUE,
                    List.of(HashtagType.ACTIVE, HashtagType.CREATIVE),
                    CHANGED_CREW_KAKAO_LINK_VALUE
            );

            crewCommandService.updateCrew(REVI.getId(), CREW.getId(), UPDATE_DTO);
            clearPersistenceContext();

            Crew FIND_CREW = crewJpaRepository.getById(CREW.getId());
            assertThat(FIND_CREW.getName()).isEqualTo(CHANGED_CREW_NAME);
            assertThat(FIND_CREW.getIntroduce()).isEqualTo(CHANGED_CREW_INTRODUCE);
            assertThat(FIND_CREW.getDetail()).isEqualTo(CHANGED_CREW_DETAIL);
            assertThat(FIND_CREW.getKakaoLink()).isEqualTo(CHANGED_CREW_KAKAO_LINK_VALUE);
            assertThat(FIND_CREW.getHashtags().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("크루장이 아니면 업데이트에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            CrewUpdateDto UPDATE_DTO = new CrewUpdateDto(
                    CHANGED_CREW_NAME_VALUE,
                    CHANGED_CREW_INTRODUCE_VALUE,
                    CHANGED_CREW_DETAIL_VALUE,
                    List.of(HashtagType.ACTIVE, HashtagType.CREATIVE),
                    CHANGED_CREW_KAKAO_LINK_VALUE
            );

            assertThatThrownBy(() -> crewCommandService.updateCrew(ANDONG.getId(), CREW.getId(), UPDATE_DTO))
                    .isExactlyInstanceOf(CrewBusinessException.InvalidPublisher.class);
        }
    }

    @Nested
    @DisplayName("Crew 삭제를 테스트한다.")
    class DeleteCrew {

        @Test
        @DisplayName("Crew 삭제에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW_WITH_IMAGE(REVI, CREW_IMAGE_LINK_VALUE));

            crewCommandService.deleteCrew(REVI.getId(), CREW.getId());

            assertThat(applicationEvents.stream(FileDeleteEvent.class)).hasSize(1);
            assertThat(crewJpaRepository.findById(CREW.getId())).isEmpty();
        }

        @Test
        @DisplayName("크루장이 아니면 삭제에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));

            assertThatThrownBy(() -> crewCommandService.deleteCrew(ANDONG.getId(), CREW.getId()))
                    .isExactlyInstanceOf(CrewBusinessException.InvalidPublisher.class);
        }
    }

    @Nested
    @DisplayName("Crew 이미지 업데이트를 테스트한다.")
    class UpdateCrewImage {

        @Test
        @DisplayName("기존 Crew 에 이미지가 없을 때, Crew 이미지 업데이트에 성공한다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            MockMultipartFile MULTIPART = PNG_MULTIPART(DEFAULT_MULTIPART_NAME, DEFAULT_ORIGINAL_FILENAME);
            when(crewS3StorageManager.upload(MULTIPART)).thenReturn(CHANGED_CREW_IMAGE_LINK_VALUE);

            crewCommandService.updateCrewImage(REVI.getId(), CREW.getId(), MULTIPART);

            assertThat(CREW.hasImage()).isTrue();
            assertThat(CREW.getImageUrl()).isEqualTo(CHANGED_CREW_IMAGE_LINK_VALUE);
        }

        @Test
        @DisplayName("기존 Crew 에 이미지가 있을 때, Crew 이미지 업데이트에 성공한다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW_WITH_IMAGE(REVI, CREW_IMAGE_LINK_VALUE));
            MockMultipartFile MULTIPART = PNG_MULTIPART(DEFAULT_MULTIPART_NAME, DEFAULT_ORIGINAL_FILENAME);
            when(crewS3StorageManager.upload(MULTIPART, CREW.getImageUrl())).thenReturn(CREW_IMAGE_LINK_VALUE);

            crewCommandService.updateCrewImage(REVI.getId(), CREW.getId(), MULTIPART);

            assertThat(CREW.hasImage()).isTrue();
            assertThat(CREW.getImageUrl()).isEqualTo(CREW_IMAGE_LINK_VALUE);
        }

        @Test
        @DisplayName("크루장이 아니면 이미지 업데이트에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            MockMultipartFile MULTIPART = PNG_MULTIPART(DEFAULT_MULTIPART_NAME, DEFAULT_ORIGINAL_FILENAME);

            assertThatThrownBy(() -> crewCommandService.updateCrewImage(ANDONG.getId(), CREW.getId(), MULTIPART))
                    .isExactlyInstanceOf(CrewBusinessException.InvalidPublisher.class);
        }
    }

    @Nested
    @DisplayName("Crew 이미지 삭제를 테스트한다.")
    class DeleteCrewImage {

        @Test
        @DisplayName("Crew 이미지 삭제에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW_WITH_IMAGE(REVI, CREW_IMAGE_LINK_VALUE));

            crewCommandService.deleteCrewImage(REVI.getId(), CREW.getId());

            assertThat(applicationEvents.stream(CrewImageDeleteEvent.class)).hasSize(1);
            assertThat(CREW.hasNotImage()).isTrue();
            assertThat(CREW.getImageUrl()).isNull();
        }

        @Test
        @DisplayName("크루장이 아니면 이미지 삭제에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));

            assertThatThrownBy(() -> crewCommandService.deleteCrewImage(ANDONG.getId(), CREW.getId()))
                    .isExactlyInstanceOf(CrewBusinessException.InvalidPublisher.class);
        }
    }
}
