package gate.api_gateway.security;
 
import gate.api_gateway.util.JwtFilter;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;

import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsConfigurationSource;

import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;
 
import java.util.Arrays;

import java.util.Collections;
 
@Configuration

@EnableWebFluxSecurity

public class SecurityConfig {
 
    private final JwtFilter jwtWebFilter;
 
    public SecurityConfig(JwtFilter jwtWebFilter) {

        this.jwtWebFilter = jwtWebFilter;

    }
 
    @Bean

    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http

                .cors(Customizer.withDefaults())

                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchanges -> exchanges

                        .pathMatchers(

                                "/user/add",

                                "/user/login",

                                "/user/register",

                                "/user/generate-device-logout-otp",

                                "/user/verify-device-logout-otp",

                                "/user/logout-all-devices/*",

                                "/user/generate-otp-change-password",

                                "/user/change-password-with-otp",

                                "/user/all"

                        ).permitAll()

                        .anyExchange().authenticated()

                )

                .addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();

    }
 
    @Bean

    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
 
        // ✅ Update frontend domain (the one hosting your frontend)

        config.setAllowedOrigins(Arrays.asList(

                "http://fusion-fe.surnoi.in",

                "https://fusion-fe.surnoi.in",

                "http://localhost:4200"

        ));
 
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        config.setAllowedHeaders(Collections.singletonList("*"));

        config.setAllowCredentials(true);

        config.setMaxAge(3600L);
 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
 
        return source;

    }

}
