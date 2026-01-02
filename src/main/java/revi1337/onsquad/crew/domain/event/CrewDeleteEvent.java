package revi1337.onsquad.crew.domain.event;

public record CrewDeleteEvent(
        Long crewId,
        String imageUrl
) {

}
