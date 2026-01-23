package revi1337.onsquad.auth.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import revi1337.onsquad.auth.security.JsonWebTokenAuthenticationProvider;
import revi1337.onsquad.auth.security.JsonWebTokenFailureHandler;
import revi1337.onsquad.auth.security.JsonWebTokenLoginFilter;
import revi1337.onsquad.auth.security.JsonWebTokenSuccessHandler;
import revi1337.onsquad.auth.security.JsonWebTokenUserDetailsService;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.token.application.JsonWebTokenManager;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final JsonWebTokenManager jsonWebTokenManager;
    private final Validator validator;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.anyRequest().permitAll())
                .addFilterAfter(loginFilter(), LogoutFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new JsonWebTokenUserDetailsService(memberRepository);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new JsonWebTokenAuthenticationProvider(passwordEncoder(), userDetailsService());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public AbstractAuthenticationProcessingFilter loginFilter() {
        JsonWebTokenLoginFilter jsonWebTokenFilter = new JsonWebTokenLoginFilter(
                authenticationManager(), objectMapper, validator
        );
        jsonWebTokenFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        jsonWebTokenFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return jsonWebTokenFilter;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new JsonWebTokenSuccessHandler(jsonWebTokenManager, objectMapper);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new JsonWebTokenFailureHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*"); // TODO 나중에 Front BaseURL 만으로 변경해야 한다.
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.LOCATION));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }
}
