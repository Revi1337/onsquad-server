package revi1337.onsquad.common.aspect;

import java.util.concurrent.TimeUnit;

public interface RequestCacheHandler {

    boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit);

}
