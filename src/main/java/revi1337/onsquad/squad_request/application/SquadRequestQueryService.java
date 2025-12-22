package revi1337.onsquad.squad_request.application;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad_category.domain.SquadCategories;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryRepository;
import revi1337.onsquad.squad_member.application.SquadMemberAccessPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_request.application.response.MySquadRequestResponse;
import revi1337.onsquad.squad_request.application.response.SquadRequestResponse;
import revi1337.onsquad.squad_request.domain.SquadRequests;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadRequestQueryService {

    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadRequestAccessPolicy squadRequestAccessPolicy;
    private final SquadRequestRepository squadRequestRepository;
    private final SquadCategoryRepository squadCategoryRepository;

    public List<SquadRequestResponse> fetchAllRequests(Long memberId, Long squadId, Pageable pageable) {
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadRequestAccessPolicy.ensureRequestListAccessible(squadMember);

        return squadRequestRepository.fetchAllBySquadId(squadId, pageable).stream()
                .map(SquadRequestResponse::from)
                .toList();
    }

    public List<MySquadRequestResponse> fetchMyRequests(Long memberId) {
        SquadRequests requests = new SquadRequests(squadRequestRepository.fetchMyRequests(memberId));
        if (requests.isNotEmpty()) {
            SquadCategories categories = new SquadCategories(squadCategoryRepository.fetchCategoriesBySquadIdIn(requests.getSquadIds()));
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
