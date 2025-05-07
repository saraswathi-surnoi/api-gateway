package gate.api_gateway.security;

//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
//import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
//// import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.util.FileCopyUtils;
//
//import java.security.KeyFactory;
//import java.security.interfaces.RSAPublicKey;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Base64;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    // Option 1: Load from application properties
//    @Value("${jwt.public-key:}")
//    private String publicKeyString;
//
//    // Option 2: JWK Set URL for OAuth providers
//    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}")
//    private String jwkSetUri;
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF protection
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/login", "/signup", "/user/add").permitAll().anyExchange()
////                        .authenticated()
//                                .permitAll()
//                )
//                 .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(withDefaults())) // Configure JWT for OAuth2
//                .build();
//    }
//
//    @Bean
//    public ReactiveJwtDecoder jwtDecoder() throws Exception {
//        // Choose one of the methods below based on your setup
//
//        // Method 1: Using a base64 encoded public key from application properties
//        if (!publicKeyString.isEmpty()) {
//            byte[] decodedKey = Base64.getDecoder().decode(publicKeyString);
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
//            return NimbusReactiveJwtDecoder.withPublicKey(rsaPublicKey).build();
//        }
//
//        // Method 2: Using a JWK Set URI (common with OAuth providers)
//        if (!jwkSetUri.isEmpty()) {
//            return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
//        }
//
//        // Method 3: Loading from a PEM file in classpath
//        try {
//            ClassPathResource resource = new ClassPathResource("public-key.pem");
//            String key = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
//
//            // Remove BEGIN/END lines and whitespace
//            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
//                    .replace("-----END PUBLIC KEY-----", "")
//                    .replaceAll("\\s", "");
//
//            byte[] decodedKey = Base64.getDecoder().decode(key);
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
//
//            return NimbusReactiveJwtDecoder.withPublicKey(rsaPublicKey).build();
//        } catch (Exception e) {
//            // Fallback to a hardcoded key (not recommended for production)
//            // Replace this with your actual base64 encoded public key (with no dashes/newlines)
//            String hardcodedKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlivFI8qB4D0y2jy0CfEqFyy46R0o7S8TKpsx5xbHKoU1VWg6QkQm+ntyIv1p4kE1sPEQO73+HY8+Bzs75XwRTYL1BmR1w8J5hmjVWjc6R2BTBGAYRPFRhor3kpM6ni2SPmNNhurEAHw7TaqszP5eUF/F9+KEBWkwVta+PZ37bwqSE4sCb1soZFrVz/UT/LF4tYpuVYt3YbqToZ3pZOZ9AX2o1GCG3xwOjkc4x0W7ezbQZdC9iftPxVHR8irOijJRRjcPDtA6vPKpzLl6CyYnsIYPd99ltwxTHjr3npfv/3Lw50bAkbT4HeLFxTx4flEoZLKO/g0bAoV2uqBhkA9xnQIDAQAB";
//            byte[] decodedKey = Base64.getDecoder().decode(hardcodedKey);
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
//
//            return NimbusReactiveJwtDecoder.withPublicKey(rsaPublicKey).build();
//        }
//    }
//}


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.FileCopyUtils;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers("/auth/login", "/user/register", "/user/add").permitAll()
//                        .anyExchange().authenticated()
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder())) // ✅ FIXED for Spring Security 6+
//                )
                .build();
    }

//    @Bean
//    public ReactiveJwtDecoder jwtDecoder() {
//        try {
//            ClassPathResource resource = new ClassPathResource("jwt-public-key.pem");
//            String key = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
//            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
//                    .replace("-----END PUBLIC KEY-----", "")
//                    .replaceAll("\\s+", "");
//
//            byte[] decoded = Base64.getDecoder().decode(key);
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
//            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
//
//            return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
//        } catch (Exception e) {
//            throw new RuntimeException("Could not load RSA public key", e);
//        }
//    }
}
