package gate.api_gateway.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JwtFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final WebClient webClient;
    private final JwtUtil jwtUtil;

    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;

    public JwtFilter(WebClient.Builder webClientBuilder, JwtUtil jwtUtil) {
        this.webClient = webClientBuilder.build();
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.debug("No Bearer token found, proceeding with filter chain");
            return chain.filter(exchange);
        }

        String jwt = authorizationHeader.substring(7);
        logger.info("Processing token: {}", jwt);

        // Validate JWT
        if (!jwtUtil.validateToken(jwt)) {
            logger.warn("Invalid JWT token");
            String message = "{\"error\": \"Unauthorized access\"}";
            byte[] bytes = message.getBytes();
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            exchange.getResponse().getHeaders().setContentLength(bytes.length);

            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        // Extract username
        String username = jwtUtil.getUsernameFromToken(jwt);
        if (username == null) {
            logger.warn("Unable to extract username from token");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Check if token is active via user service
        return webClient.get()
                .uri(userServiceUrl + "/user/IsdeviceAlreadyLooged/{token}", jwt)
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> {
                    if (response.getStatusCode() == HttpStatus.OK) {
                        logger.info("Token is active for username: {}", username);

                        // Set authentication context
                        UserDetails userDetails = new User(username, "", Collections.emptyList());
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                    } else if (response.getStatusCode() == HttpStatus.EXPECTATION_FAILED) {
                        logger.warn("Token not found or device not logged in");
                        String message = "{\"error\": \"device not logged in\"}";
                        byte[] bytes = message.getBytes();
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        return exchange.getResponse().setComplete();
                    } else {
                        // Propagate other statuses as errors
                        return Mono.error(new RuntimeException("Unexpected status: " + response.getStatusCode()));
                    }
                })
                .onErrorResume(ex -> {
                    logger.error("Error calling user service: {}", ex.getMessage());
                    // Optional: Only treat specific errors as unauthorized
                    if (ex instanceof WebClientResponseException webEx && webEx.getStatusCode() == HttpStatus.EXPECTATION_FAILED) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    return exchange.getResponse().setComplete();
                });

    }
}