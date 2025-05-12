//package gate.api_gateway.filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureException;
//
//import java.util.List;
//
//@Component
//public class JwtAuthFilter implements GlobalFilter, Ordered {
//
//    private static final List<String> openEndpoints = List.of(
//            "/auth/login",
//            "/user/register",
//            "/user/add",
//            "/user/login",
//            "/user/**"
//    );
//
//    // The secret key used for HS256 signing
//    private static final String SECRET_KEY = "your-secret-key-here"; // Replace with your actual secret key
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String path = exchange.getRequest().getURI().getPath();
//        System.out.println("Requested path: " + path); // ✅ Debug log
//
//        if (openEndpoints.stream().anyMatch(path::endsWith)) {
//            return chain.filter(exchange);
//        }
//
//        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        String token = authHeader.substring(7); // Extract the token part from "Bearer token"
//        try {
//            // Validate the JWT token using the secret key and HS256 algorithm
//            Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY)  // Using the secret key for HS256
//                    .build()
//                    .parseClaimsJws(token); // This will throw an exception if the token is invalid
//
//        } catch (SignatureException e) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        } catch (Exception e) {
//            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
//            return exchange.getResponse().setComplete();
//        }
//
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return -1; // Ensure that this filter runs before others
//    }
//}
