package revi1337.onsquad.squad_request.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.squad.domain.model.SquadLinkableGroup;
import revi1337.onsquad.squad_category.application.SquadCategoryAccessor;
import revi1337.onsquad.squad_category.domain.model.SquadCategories;
import revi1337.onsquad.squad_member.application.SquadMemberAccessor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_request.application.response.MySquadRequestResponse;
import revi1337.onsquad.squad_request.application.response.SquadRequestResponse;
import revi1337.onsquad.squad_request.domain.SquadRequestPolicy;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SquadRequestQueryService {

    private final SquadRequestAccessor squadRequestAccessor;
    private final SquadMemberAccessor squadMemberAccessor;
    private final SquadCategoryAccessor squadCategoryAccessor;

    public PageResponse<SquadRequestResponse> fetchAllRequests(Long memberId, Long squadId, Pageable pageable) {
        SquadMember me = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        SquadRequestPolicy.ensureReadRequests(me);
        Page<SquadRequestResponse> response = squadRequestAccessor.fetchAllBySquadId(squadId, pageable)
                .map(SquadRequestResponse::from);

        return PageResponse.from(response);
    }

    public PageResponse<MySquadRequestResponse> fetchMyRequests(Long memberId, Pageable pageable) {
        Page<MySquadRequestResponse> response = squadRequestAccessor.fetchMyRequests(memberId, pageable)
                .map(MySquadRequestResponse::from);

        SquadLinkableGroup<MySquadRequestResponse> group = new SquadLinkableGroup<>(response.getContent());
        if (group.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(group.getSquadIds());
            group.linkCategories(categories);
        }

        return PageResponse.from(response);
    }
}
