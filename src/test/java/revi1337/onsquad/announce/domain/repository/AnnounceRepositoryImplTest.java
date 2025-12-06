package revi1337.onsquad.announce.domain.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;

@ExtendWith(MockitoExtension.class)
class AnnounceRepositoryImplTest {

    @Mock
    private AnnounceJpaRepository announceJpaRepository;

    @Mock
    private AnnounceQueryDslRepository announceQueryDslRepository;

    @Mock
    private AnnounceCacheRepository announceCacheRepository;

    @InjectMocks
    private AnnounceRepositoryImpl announceRepository;

    @Test
    @DisplayName("save 위임에 성공한다.")
    void save() {
        Announce ANNOUNCE = mock(Announce.class);

        announceRepository.save(ANNOUNCE);

        verify(announceJpaRepository).save(ANNOUNCE);
    }

    @Test
    @DisplayName("saveAndFlush 위임에 성공한다.")
    void saveAndFlush() {
        Announce ANNOUNCE = mock(Announce.class);

        announceRepository.saveAndFlush(ANNOUNCE);

        verify(announceJpaRepository).saveAndFlush(ANNOUNCE);
    }

    @Nested
    @DisplayName("findByIdAndCrewId & getByIdAndCrewId 위임을 테스트한다.")
    class FindAndGetByIdAndCrewId {

        @Test
        @DisplayName("findByIdAndCrewId 위임에 성공한다.")
        void findByIdAndCrewId() {
            Long DUMMY_ANNOUNCE_ID = 1L;
            Long DUMMY_CREW_ID = 1L;

            announceRepository.findByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID);

            verify(announceJpaRepository).findByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID);
        }

        @Test
        @DisplayName("getByIdAndCrewId 위임에 성공한다.")
        void getByIdAndCrewId() {
            Long DUMMY_ANNOUNCE_ID = 1L;
            Long DUMMY_CREW_ID = 1L;
            Announce ANNOUNCE = mock(Announce.class);
            when(announceJpaRepository.findByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID))
                    .thenReturn(Optional.of(ANNOUNCE));

            announceRepository.findByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID);

            verify(announceJpaRepository).findByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID);
        }

        @Test
        @DisplayName("getByIdAndCrewId 를 호출했을 때, findByIdAndCrewId 의 결과가 없으면 실패한다.")
        void getByIdAndCrewIdFail() {
            Long DUMMY_ANNOUNCE_ID = 1L;
            Long DUMMY_CREW_ID = 1L;
            when(announceJpaRepository.findByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> announceRepository.getByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID))
                    .isExactlyInstanceOf(AnnounceBusinessException.NotFoundById.class);
        }
    }

    @Test
    @DisplayName("fetchAllByCrewId 위임에 성공한다.")
    void fetchAllByCrewId() {
        Long DUMMY_CREW_ID = 1L;
        PageRequest PAGE_REQUEST = mock(PageRequest.class);

        announceRepository.fetchAllByCrewId(DUMMY_CREW_ID, PAGE_REQUEST);

        verify(announceQueryDslRepository).fetchAllByCrewId(DUMMY_CREW_ID, PAGE_REQUEST);
    }

    @Test
    @DisplayName("fetchCacheByCrewIdAndId 위임에 성공한다.")
    void fetchCacheByCrewIdAndId() {
        Long DUMMY_CREW_ID = 1L;
        Long DUMMY_ANNOUNCE_ID = 1L;
        AnnounceDomainDto DOMAIN_DTO = mock(AnnounceDomainDto.class);
        when(announceCacheRepository.fetchCacheByCrewIdAndId(anyLong(), anyLong()))
                .thenReturn(DOMAIN_DTO);

        announceRepository.fetchCacheByCrewIdAndId(DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID);

        verify(announceCacheRepository).fetchCacheByCrewIdAndId(DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID);
    }
}
