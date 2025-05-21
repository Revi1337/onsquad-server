package revi1337.onsquad.common.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RequestCacheHandlerExecutionChain implements RequestCacheHandler {

    private final List<RequestCacheHandler> requestCacheHandlers =
            new ArrayList<>(Collections.singletonList(new ExpiringMapRequestCacheHandler()));

    public void addRequestCacheHandlerBefore(RequestCacheHandler requestCacheHandler,
                                             Class<? extends RequestCacheHandler> clazz) {
        for (int i = 0; i < requestCacheHandlers.size(); i++) {
            RequestCacheHandler currentHandler = requestCacheHandlers.get(i);
            if (clazz.isAssignableFrom(currentHandler.getClass())) {
                requestCacheHandlers.add(i, requestCacheHandler);
                return;
            }
        }
    }

    @Override
    public Boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit) {
        return requestCacheHandlers.stream()
                .map(handler -> handler.isFirstRequest(key, value, timeout, unit))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[모든 캐싱 후보군을 사용할 수 없습니다.]"));
    }

    public List<RequestCacheHandler> getRequestCacheHandlers() {
        return requestCacheHandlers.stream().toList();
    }
}
