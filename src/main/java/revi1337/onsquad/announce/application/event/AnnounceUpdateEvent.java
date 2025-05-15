package revi1337.onsquad.announce.application.event;

public record AnnounceUpdateEvent(
        Long crewId,
        Long announceId
) {
    public String getEventName() {
        return this.getClass().getSimpleName();
    }
}
