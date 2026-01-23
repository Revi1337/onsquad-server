package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.SquadLinkableGroup;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.domain.result.SquadResult;
import revi1337.onsquad.squad.domain.result.SquadWithLeaderStateResult;
import revi1337.onsquad.squad.error.SquadBusinessException;
import revi1337.onsquad.squad.error.SquadErrorCode;

@Component
@RequiredArgsConstructor
public class SquadAccessor {

    private final SquadRepository squadRepository;

    public Squad getById(Long squadId) {
        return squadRepository.findById(squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
    }

    public Squad getByIdForUpdate(Long squadId) {
        return squadRepository.findByIdForUpdate(squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
    }

    public Squad getWithDetailById(Long squadId) {
        return squadRepository.findSquadWithDetailById(squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
    }

    public Squad getReferenceById(Long squadId) {
        return squadRepository.getReferenceById(squadId);
    }

    public SquadLinkableGroup<SquadWithLeaderStateResult> fetchManageList(Long memberId, Long crewId, Pageable pageable) {
        return new SquadLinkableGroup<>(squadRepository.fetchManageList(memberId, crewId, pageable));
    }

    public SquadLinkableGroup<SquadResult> fetchSquadsWithDetailByCrewIdAndCategory(Long crewId, CategoryType categoryType, Pageable pageable) {
        return new SquadLinkableGroup<>(squadRepository.fetchSquadsWithDetailByCrewIdAndCategory(crewId, categoryType, pageable));
    }
}
