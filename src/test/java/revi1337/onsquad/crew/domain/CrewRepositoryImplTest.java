package revi1337.onsquad.crew.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;

@ExtendWith(MockitoExtension.class)
class CrewRepositoryImplTest {

    @Mock
    private CrewJpaRepository crewJpaRepository;

    @Mock
    private CrewQueryDslRepository crewQueryDslRepository;

    @InjectMocks
    private CrewRepositoryImpl crewRepositoryImpl;

    @Test
    @DisplayName("save 위임에 성공한다.")
    void save() {
        Crew crew = mock(Crew.class);
        when(crewJpaRepository.save(crew)).thenReturn(crew);

        crewRepositoryImpl.save(crew);

        verify(crewJpaRepository).save(crew);
    }

    @Test
    @DisplayName("saveAndFlush 위임에 성공한다.")
    void saveAndFlush() {
        Crew crew = mock(Crew.class);
        when(crewJpaRepository.saveAndFlush(crew)).thenReturn(crew);

        crewRepositoryImpl.saveAndFlush(crew);

        verify(crewJpaRepository).saveAndFlush(crew);
    }

    @Test
    @DisplayName("deleteById 위임에 성공한다.")
    void deleteById() {
        Long dummyCrewId = 1L;
        doNothing().when(crewJpaRepository).deleteById(dummyCrewId);

        crewRepositoryImpl.deleteById(dummyCrewId);

        verify(crewJpaRepository).deleteById(dummyCrewId);
    }

    @Nested
    @DisplayName("findById & getById 위임을 테스트한다.")
    class FindById {

        @Test
        @DisplayName("findById 위임에 성공한다.")
        void success1() {
            Long dummyCrewId = 1L;
            when(crewJpaRepository.findById(dummyCrewId)).thenReturn(Optional.empty());

            crewRepositoryImpl.findById(dummyCrewId);

            verify(crewJpaRepository).findById(dummyCrewId);
        }

        @Test
        @DisplayName("getById 위임에 성공한다.")
        void success2() {
            Long dummyCrewId = 1L;
            when(crewJpaRepository.findById(dummyCrewId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> crewRepositoryImpl.getById(dummyCrewId))
                    .isExactlyInstanceOf(CrewBusinessException.NotFoundById.class);
        }
    }

    @Test
    @DisplayName("existsByName 위임에 성공한다.")
    void existsByName() {
        Name dummyCrewName = new Name("dummy-name");
        when(crewJpaRepository.existsByName(dummyCrewName)).thenReturn(true);

        crewRepositoryImpl.existsByName(dummyCrewName);

        verify(crewJpaRepository).existsByName(dummyCrewName);
    }

    @Nested
    @DisplayName("findCrewById & getCreById 위임을 테스트한다.")
    class FindCrewById {

        @Test
        @DisplayName("findById 위임에 성공한다.")
        void success1() {
            Long dummyCrewId = 1L;
            when(crewQueryDslRepository.findCrewById(dummyCrewId)).thenReturn(Optional.empty());

            crewRepositoryImpl.findCrewById(dummyCrewId);

            verify(crewQueryDslRepository).findCrewById(dummyCrewId);
        }

        @Test
        @DisplayName("getCrewById 위임에 성공한다.")
        void success() {
            Long dummyCrewId = 1L;
            when(crewQueryDslRepository.findCrewById(dummyCrewId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> crewRepositoryImpl.getCrewById(dummyCrewId))
                    .isExactlyInstanceOf(CrewBusinessException.NotFoundById.class);
        }
    }

    @Test
    @DisplayName("fetchCrewsByName 위임에 성공한다.")
    void fetchCrewsByName() {
        String dummyCrewName = "dummy-name";
        Pageable pageable = mock(Pageable.class);
        List<CrewDomainDto> content = List.of(mock(CrewDomainDto.class), mock(CrewDomainDto.class));
        Page<CrewDomainDto> results = new PageImpl<>(content, pageable, content.size());
        when(crewQueryDslRepository.fetchCrewsByName(dummyCrewName, pageable)).thenReturn(results);

        crewRepositoryImpl.fetchCrewsByName(dummyCrewName, pageable);

        verify(crewQueryDslRepository).fetchCrewsByName(dummyCrewName, pageable);
    }

    @Test
    @DisplayName("fetchOwnedByMemberId 위임에 성공한다.")
    void fetchOwnedByMemberId() {
        Long dummyMemberId = 1L;
        Pageable pageable = mock(Pageable.class);
        List<CrewDomainDto> content = List.of(mock(CrewDomainDto.class), mock(CrewDomainDto.class));
        Page<CrewDomainDto> results = new PageImpl<>(content, pageable, content.size());
        when(crewQueryDslRepository.fetchCrewsByMemberId(dummyMemberId, pageable)).thenReturn(results);

        crewRepositoryImpl.fetchOwnedByMemberId(dummyMemberId, pageable);

        verify(crewQueryDslRepository).fetchCrewsByMemberId(dummyMemberId, pageable);
    }

    @Test
    @DisplayName("fetchParticipantsByMemberId 위임에 성공한다.")
    void fetchParticipantsByMemberId() {
        Long dummyMemberId = 1L;
        List<EnrolledCrewDomainDto> content = List.of(mock(EnrolledCrewDomainDto.class), mock(EnrolledCrewDomainDto.class));
        when(crewQueryDslRepository.fetchEnrolledCrewsByMemberId(dummyMemberId)).thenReturn(content);

        crewRepositoryImpl.fetchParticipantsByMemberId(dummyMemberId);

        verify(crewQueryDslRepository).fetchEnrolledCrewsByMemberId(dummyMemberId);
    }
}
