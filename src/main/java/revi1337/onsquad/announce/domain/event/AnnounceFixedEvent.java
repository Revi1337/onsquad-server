package revi1337.onsquad.announce.domain.event;

public record AnnounceFixedEvent(
        Long crewId,
        Long announceId
) {

    public String getEventName() {
        return this.getClass().getSimpleName();
    }
}
