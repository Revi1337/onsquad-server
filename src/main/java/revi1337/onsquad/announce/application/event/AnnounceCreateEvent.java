package revi1337.onsquad.announce.application.event;

public record AnnounceCreateEvent(
        Long crewId
) {
    public String getEventName() {
        return this.getClass().getSimpleName();
    }
}
