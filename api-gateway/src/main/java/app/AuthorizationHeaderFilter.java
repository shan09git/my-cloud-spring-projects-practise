package app;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {


    @Value("${token.secret}")
    private String tokenSecret;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return
                (exchange, chain) -> {

                    var request = exchange.getRequest();
                    //System.out.println("request = " + request);
                    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        return onError(exchange, "No Header value", HttpStatus.UNAUTHORIZED);
                    }

                    var authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).getFirst();
                    // System.out.println("authorizationHeader = " + authorizationHeader);

                    var jwtToken = authorizationHeader.replace("Bearer ", "");

                    //System.out.println("jwtToken = " + jwtToken);

                    if (!isJwtTokenValid(jwtToken)) {
                        return onError(exchange, "Jwt Token Not Valid", HttpStatus.UNAUTHORIZED);
                    }

                    return chain.filter(exchange);
                };
    }

    private boolean isJwtTokenValid(String jwtToken) {

        try {
            var secretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(tokenSecret.getBytes()));
            var jwtParser = Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build();

            var claims = jwtParser.parseSignedClaims(jwtToken).getPayload();
            var subject = claims.get("jti", String.class);

            //System.out.println("subject = " + subject);

            if (subject == null) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        var response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }


}
