package revi1337.onsquad.common.aspect;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RequestCacheHandlerExecutionChain implements RequestCacheHandler {

    private final List<RequestCacheHandler> requestCacheHandlers = new ArrayList<>();

    public void addRequestCacheHandler(RequestCacheHandler... requestCacheHandler) {
        CollectionUtils.mergeArrayIntoCollection(requestCacheHandler, requestCacheHandlers);
    }

    @Override
    public boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit) {
        if (requestCacheHandlers.isEmpty()) {
            return true;
        }
        int throwCount = requestCacheHandlers.size();
        for (RequestCacheHandler requestCacheHandler : requestCacheHandlers) {
            try {
                if (requestCacheHandler.isFirstRequest(key, value, timeout, unit)) {
                    return true;
                }
            } catch (Throwable ignored) {
                throwCount -= 1;
            }
        }
        if (throwCount == 0) {
            throw new IllegalStateException("모든 캐싱 전략에 실패하였습니다.");
        }

        return false;
    }
}
