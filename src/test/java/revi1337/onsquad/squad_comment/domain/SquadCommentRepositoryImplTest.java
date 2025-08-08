package revi1337.onsquad.squad_comment.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@ExtendWith(MockitoExtension.class)
class SquadCommentRepositoryImplTest {

    @Mock
    private SquadCommentJpaRepository squadCommentJpaRepository;

    @Mock
    private SquadCommentQueryDslRepository squadCommentQueryDslRepository;

    @Mock
    private SquadCommentJdbcRepository squadCommentJdbcRepository;

    @InjectMocks
    private SquadCommentRepositoryImpl squadCommentRepositoryImpl;

    @Test
    @DisplayName("save 위임에 성공한다.")
    void save() {
        SquadComment squadComment = mock(SquadComment.class);

        squadCommentRepositoryImpl.save(squadComment);

        verify(squadCommentJpaRepository).save(squadComment);
    }

    @Test
    @DisplayName("findById 위임에 성공한다.")
    void findById() {
        Long commentId = 1L;

        squadCommentRepositoryImpl.findById(commentId);

        verify(squadCommentJpaRepository).findById(commentId);
    }

    @Test
    @DisplayName("findByIdAndSquadIdAndCrewId 위임에 성공한다.")
    void findByIdAndSquadIdAndCrewId() {
        Long commentId = 1L;
        Long squadId = 2L;
        Long crewId = 3L;

        squadCommentRepositoryImpl.findByIdAndSquadIdAndCrewId(commentId, squadId, crewId);

        verify(squadCommentJpaRepository).findByIdAndSquadIdAndCrewId(commentId, squadId, crewId);
    }

    @Test
    @DisplayName("fetchAllParentsBySquadId 위임에 성공한다.")
    void fetchAllParentsBySquadId() {
        Long squadId = 2L;
        PageRequest pageRequest = mock(PageRequest.class);
        when(squadCommentQueryDslRepository.fetchAllParentsBySquadId(squadId, pageRequest))
                .thenReturn(new ArrayList<>());

        squadCommentRepositoryImpl.fetchAllParentsBySquadId(squadId, pageRequest);

        verify(squadCommentQueryDslRepository).fetchAllParentsBySquadId(squadId, pageRequest);
    }

    @Test
    @DisplayName("fetchAllChildrenByParentIdIn 위임에 성공한다.")
    void fetchAllChildrenByParentIdIn() {
        Collection<Long> parentIds = Set.of(1L, 2L, 3L);
        int childSize = 3;
        List<SquadCommentDomainDto> children = List.of();
        when(squadCommentJdbcRepository.fetchAllChildrenByParentIdIn(parentIds, childSize))
                .thenReturn(children);

        squadCommentRepositoryImpl.fetchAllChildrenByParentIdIn(parentIds, childSize);

        verify(squadCommentJdbcRepository).fetchAllChildrenByParentIdIn(parentIds, childSize);
    }

    @Test
    @DisplayName("fetchAllChildrenBySquadIdAndParentId 위임에 성공한다.")
    void fetchAllChildrenBySquadIdAndParentId() {
        Long squadId = 2L;
        Long parentId = 3L;
        PageRequest pageRequest = mock(PageRequest.class);
        List<SquadCommentDomainDto> children = List.of();
        when(squadCommentQueryDslRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageRequest))
                .thenReturn(children);

        squadCommentRepositoryImpl.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageRequest);

        verify(squadCommentQueryDslRepository).fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageRequest);
    }

    @Test
    @DisplayName("findAllWithMemberBySquadId 위임에 성공한다.")
    void findAllWithMemberBySquadId() {
        Long squadId = 2L;
        List<SquadCommentDomainDto> children = List.of();
        when(squadCommentQueryDslRepository.findAllWithMemberBySquadId(squadId))
                .thenReturn(children);

        squadCommentRepositoryImpl.findAllWithMemberBySquadId(squadId);

        verify(squadCommentQueryDslRepository).findAllWithMemberBySquadId(squadId);
    }
}
