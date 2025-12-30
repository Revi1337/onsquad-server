package revi1337.onsquad.common.config.web;

import jakarta.servlet.Filter;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new SystemMaintenanceFilter(exceptionResolver));
        filterRegistrationBean.setUrlPatterns(List.of("/*"));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }
}
