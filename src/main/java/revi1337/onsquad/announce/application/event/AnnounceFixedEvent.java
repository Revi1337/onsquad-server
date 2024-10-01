package revi1337.onsquad.announce.application.event;

public record AnnounceFixedEvent(
        Long crewId
) {
    public String getEventName() {
        return this.getClass().getSimpleName();
    }
}
