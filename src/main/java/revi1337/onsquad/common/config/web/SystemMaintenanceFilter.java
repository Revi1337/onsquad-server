package revi1337.onsquad.common.config.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerExceptionResolver;
import revi1337.onsquad.common.error.CommonBusinessException;
import revi1337.onsquad.common.error.CommonErrorCode;

@RequiredArgsConstructor
public class SystemMaintenanceFilter implements Filter {

    private static final LocalTime FROM = LocalTime.MIDNIGHT;
    private static final LocalTime TO = LocalTime.of(0, 5);
    private static final Predicate<LocalTime> IS_IN_MAINTENANCE = now -> !now.isBefore(FROM) && !now.isAfter(TO);

    private final HandlerExceptionResolver exceptionResolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (IS_IN_MAINTENANCE.test(LocalTime.now())) {
            exceptionResolver.resolveException(
                    (HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse,
                    null, new CommonBusinessException.MaintenanceTime(CommonErrorCode.MAINTENANCE_TIME, FROM.toString(), TO.toString())
            );
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
