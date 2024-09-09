package revi1337.onsquad.crew.presentation.dto.response;

public record DuplicateCrewNameResponse(
        boolean duplicate
) {
    public static DuplicateCrewNameResponse of(boolean duplicate) {
        return new DuplicateCrewNameResponse(duplicate);
    }
}
