package revi1337.onsquad.announce.domain;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.common.PersistenceLayerTestSupport;

@Import({AnnounceRepositoryImpl.class})
class AnnounceRepositoryImplTest extends PersistenceLayerTestSupport {

    @MockBean
    private AnnounceCacheRepository announceCacheRepository;

    @MockBean
    private AnnounceJpaRepository announceJpaRepository;

    @MockBean
    private AnnounceQueryDslRepository announceQueryDslRepository;

    @Autowired
    private AnnounceRepository announceRepository;

    @Test
    @DisplayName("save 위임에 성공한다.")
    void success1() {
        Announce ANNOUNCE = ANNOUNCE();

        announceRepository.save(ANNOUNCE);

        verify(announceJpaRepository).save(ANNOUNCE);
    }

    @Test
    @DisplayName("saveAndFlush 위임에 성공한다.")
    void success2() {
        Announce ANNOUNCE = ANNOUNCE();

        announceRepository.saveAndFlush(ANNOUNCE);

        verify(announceJpaRepository).saveAndFlush(ANNOUNCE);
    }

    @Test
    @DisplayName("findByIdAndCrewId 위임에 성공한다.")
    void success3() {
        Long DUMMY_ANNOUNCE_ID = 1L;
        Long DUMMY_CREW_ID = 1L;

        announceRepository.findByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID);

        verify(announceJpaRepository).findByIdAndCrewId(DUMMY_ANNOUNCE_ID, DUMMY_CREW_ID);
    }

    @Test
    @DisplayName("fetchAllByCrewId 위임에 성공한다.")
    void success4() {
        Long DUMMY_CREW_ID = 1L;
        PageRequest PAGE_REQEST = PageRequest.of(0, 10);

        announceRepository.fetchAllByCrewId(DUMMY_CREW_ID, PAGE_REQEST);

        verify(announceQueryDslRepository).fetchAllByCrewId(DUMMY_CREW_ID, PAGE_REQEST);
    }

    @Test
    @DisplayName("fetchCacheByCrewIdAndId 위임에 성공한다.")
    void success5() {
        Long DUMMY_CREW_ID = 1L;
        Long DUMMY_ANNOUNCE_ID = 1L;
        AnnounceDomainDto MOCKED = mock(AnnounceDomainDto.class);
        when(announceCacheRepository.fetchCacheByCrewIdAndId(anyLong(), anyLong()))
                .thenReturn(MOCKED);

        announceRepository.fetchCacheByCrewIdAndId(DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID);

        verify(announceCacheRepository).fetchCacheByCrewIdAndId(DUMMY_CREW_ID, DUMMY_ANNOUNCE_ID);
    }
}
