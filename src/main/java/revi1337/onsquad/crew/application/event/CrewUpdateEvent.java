package revi1337.onsquad.crew.application.event;

public record CrewUpdateEvent(
        Long crewId,
        byte[] fileContent,
        String originalFilename
) {
}
