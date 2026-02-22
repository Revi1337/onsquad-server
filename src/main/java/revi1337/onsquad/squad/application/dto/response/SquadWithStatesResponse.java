package revi1337.onsquad.squad.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadWithStatesResponse(
        SquadStates states,
        Long id,
        String title,
        String content,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        List<String> categories,
        SimpleMemberResponse leader
) {

    public static SquadWithStatesResponse from(
            boolean alreadyParticipant,
            Boolean isLeader,
            boolean canSeeParticipants,
            Boolean canLeave,
            boolean canDelete,
            Squad squad
    ) {
        return new SquadWithStatesResponse(
                SquadStates.of(
                        alreadyParticipant,
                        isLeader,
                        canSeeParticipants,
                        canLeave,
                        canDelete
                ),
                squad.getId(),
                squad.getTitle().getValue(),
                squad.getContent().getValue(),
                squad.getCapacity(),
                squad.getRemain(),
                squad.getAddress().getValue(),
                squad.getAddress().getDetail(),
                squad.getKakaoLink(),
                squad.getDiscordLink(),
                squad.getCategories().stream()
                        .map(SquadCategory::getCategory)
                        .map(Category::getCategoryType)
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberResponse.from(squad.getMember())
        );
    }
}
