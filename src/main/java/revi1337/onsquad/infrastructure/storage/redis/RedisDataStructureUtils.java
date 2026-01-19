package revi1337.onsquad.infrastructure.storage.redis;

public abstract class RedisDataStructureUtils {

    private RedisDataStructureUtils() {
    }

    public static int toZSetStart(int startRank) {
        return Math.max(0, startRank - 1);
    }

    public static int toZSetEnd(int endRank) {
        if (endRank < 0) {
            return -1;
        }

        return Math.max(0, endRank - 1);
    }

    public static ZSetRange toZSetRange(int startRank, int endRank) {
        int start = toZSetStart(startRank);
        int end = toZSetEnd(endRank);

        if (end != -1 && start > end) {
            end = start;
        }

        return new ZSetRange(start, end);
    }

    public record ZSetRange(int start, int end) {

    }
}
