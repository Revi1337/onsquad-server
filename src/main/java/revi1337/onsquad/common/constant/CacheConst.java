package revi1337.onsquad.common.constant;

public abstract class CacheConst {

    public static final String CREW_ANNOUNCES = "crew-announces";
    public static final String CREW_ANNOUNCE = "crew-announce";
    public static final String CREW_STATISTIC = "crew-statistic";
    public static final String CREW_TOP_USERS = "crew-top-users";
    public static final String REFRESH_TOKEN = "refresh";
    public static final String VERIFICATION_CODE = "verification-code";

    public static abstract class CacheFormat {

        public static final String PREFIX = "onsquad:%s:";
        public static final String SIMPLE = "onsquad:%s";
        public static final String COMPLEX = "onsquad:%s:%s";

    }
}
