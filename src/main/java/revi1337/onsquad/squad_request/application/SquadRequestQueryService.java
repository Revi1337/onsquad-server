package revi1337.onsquad.squad_request.application;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.squad_category.application.SquadCategoryAccessor;
import revi1337.onsquad.squad_category.domain.SquadCategories;
import revi1337.onsquad.squad_member.application.SquadMemberAccessor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_request.application.response.MySquadRequestResponse;
import revi1337.onsquad.squad_request.application.response.SquadRequestResponse;
import revi1337.onsquad.squad_request.domain.SquadRequestPolicy;
import revi1337.onsquad.squad_request.domain.SquadRequests;

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

        return PageResponse.from(squadRequestAccessor.fetchAllBySquadId(squadId, pageable).map(SquadRequestResponse::from));
    }

    public List<MySquadRequestResponse> fetchMyRequests(Long memberId) {
        SquadRequests requests = squadRequestAccessor.fetchMyRequests(memberId);
        if (requests.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(requests.getSquadIds());
            Map<Long, List<CategoryType>> categoryMap = categories.groupBySquadId();
            return requests.values().stream()
                    .map(request -> MySquadRequestResponse.from(request, categoryMap.get(request.getSquadId())))
                    .toList();
        }

        return requests.values().stream()
                .map(MySquadRequestResponse::from)
                .toList();
    }
}
