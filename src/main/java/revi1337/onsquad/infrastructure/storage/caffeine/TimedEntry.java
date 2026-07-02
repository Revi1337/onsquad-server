package revi1337.onsquad.infrastructure.storage.caffeine;

public record TimedEntry<V>(V value, long ttlNanos) {}
