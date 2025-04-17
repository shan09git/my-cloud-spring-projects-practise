package app.myappusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import static java.time.Instant.now;

public class UserAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final Environment environment;

    public UserAuthFilter(AuthenticationManager authenticationManager, Environment environment) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            var authModel = new ObjectMapper().readValue(request.getInputStream(), AuthModel.class);

            var authentication = getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(authModel.getEmail(),
                            authModel.getPassword(), new ArrayList<>()));
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        var authenticatedUser = (AuthUser) authResult.getPrincipal();

        var secretKeys = Keys
                .hmacShaKeyFor(Base64.getEncoder()
                        .encode(environment.getProperty("token.secret").getBytes()));

        var jwtToken = Jwts.builder()
                .id(authenticatedUser.getUsername())
                .expiration(Date.from(now().plusMillis(Long.parseLong(environment.getProperty("token.expiration")))))
                .issuedAt(Date.from(now()))
                .signWith(secretKeys)
                .compact();

        response.addHeader("token", jwtToken);
        response.addHeader("user", authenticatedUser.getUsername());
    }
}
