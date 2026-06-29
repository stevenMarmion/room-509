package com.littlefish.app.config;

import com.littlefish.app.service.JwtService;

import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;

/**
 * Security configuration that disables authentication for all @WebMvcTest slices.
 *
 * Import it in each controller test with:
 *   @Import(TestSecurityConfig.class)
 *
 * This overrides the production SecurityConfig so that every endpoint is
 * accessible without a cookie / JWT in the test context. The real JwtAuthFilter
 * component still runs (it is picked up by @WebMvcTest's filter auto-detection),
 * but is harmless here since it only relies on the mocked JwtService below and
 * simply lets requests through when there is no auth cookie.
 */
@TestConfiguration
@EnableWebSecurity
@Import(JacksonAutoConfiguration.class)
public class SecurityConfigTest {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Primary
    public JwtService jwtService() {
        return mock(JwtService.class);
    }
}
