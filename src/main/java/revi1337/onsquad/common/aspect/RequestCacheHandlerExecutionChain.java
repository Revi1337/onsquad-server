package revi1337.onsquad.common.aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RequestCacheHandlerExecutionChain implements RequestCacheHandler {

    private final List<RequestCacheHandler> requestCacheHandlers = new ArrayList<>();

    @Override
    public Boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit) {
        return requestCacheHandlers.stream()
                .map(handler -> handler.isFirstRequest(key, value, timeout, unit))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[모든 캐싱 후보군을 사용할 수 없습니다.]"));
    }

    public void addBefore(RequestCacheHandler requestCacheHandler, Class<? extends RequestCacheHandler> clazz) {
        for (int i = 0; i < requestCacheHandlers.size(); i++) {
            RequestCacheHandler currentHandler = requestCacheHandlers.get(i);
            if (clazz.isAssignableFrom(currentHandler.getClass())) {
                requestCacheHandlers.add(i, requestCacheHandler);
                return;
            }
        }
        addFirst(requestCacheHandler);
    }

    public void addAfter(RequestCacheHandler requestCacheHandler, Class<? extends RequestCacheHandler> clazz) {
        for (int i = 0; i < requestCacheHandlers.size(); i++) {
            RequestCacheHandler currentHandler = requestCacheHandlers.get(i);
            if (clazz.isAssignableFrom(currentHandler.getClass())) {
                if (i + 1 < requestCacheHandlers.size()) {
                    requestCacheHandlers.add(i + 1, requestCacheHandler);
                } else {
                    addLast(requestCacheHandler);
                }
                return;
            }
        }
        addFirst(requestCacheHandler);
    }

    public void addFirst(RequestCacheHandler requestCacheHandler) {
        if (requestCacheHandlers.isEmpty()) {
            requestCacheHandlers.add(requestCacheHandler);
            return;
        }

        requestCacheHandlers.add(0, requestCacheHandler);
    }

    public void addLast(RequestCacheHandler requestCacheHandler) {
        requestCacheHandlers.add(requestCacheHandler);
    }

    public List<RequestCacheHandler> getRequestCacheHandlers() {
        return requestCacheHandlers.stream().toList();
    }
}
