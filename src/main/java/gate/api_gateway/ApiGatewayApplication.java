package gate.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Component
	public class JwtAuthFilter implements GlobalFilter {
		// Inject JwtUtil and validate
		public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
			String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
			// If token invalid → block
			return chain.filter(exchange);
		}
	}

}
