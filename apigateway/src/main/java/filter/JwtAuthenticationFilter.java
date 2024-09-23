package filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JWTVerifier jwtVerifier;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return chain.filter(exchange);
        }
        String token = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
        if (!token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        token = token.substring(7);
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("username", decodedJWT.getSubject())
                    .build();
            exchange = exchange.mutate().request(mutatedRequest).build();
        } catch (JWTVerificationException e) {
            return Mono.error(new RuntimeException("Invalid Token"));
        }
        return chain.filter(exchange);
    }
}
